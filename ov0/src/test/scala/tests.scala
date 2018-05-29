package Core
import chisel3._
import chisel3.util._
import chisel3.core.Input
import chisel3.iotesters._
import org.scalatest.{Matchers, FlatSpec}


class daisyVectorTest(c: daisyVector, inputs: List[(Int, Int, Int)]) extends PeekPokeTester(c) {

  (inputs).foreach {
    case(enIn, dataIn, dataOut) => {
      poke(c.io.readEnable, enIn)
      poke(c.io.dataIn, dataIn)
      expect(c.io.dataOut, dataOut)
      step(1)
    }
  }

}

class daisyDotTest(c: daisyDot, inputs: List[(Int, Int, Option[Int], Int)]) extends PeekPokeTester(c) {

  (inputs).foreach {
    case(inA, inB, dataOut, dataValid) => {
      poke(c.io.dataInA, inA)
      poke(c.io.dataInB, inB)
      dataOut.foreach { expect(c.io.dataOut, _) }
      expect(c.io.outputValid, dataValid)
      step(1)
    }
  }

}

class daisyGridTest(c: daisyGrid, inputs: List[(Int, Int, Int, Int)]) extends PeekPokeTester(c) {

  (inputs).foreach {
    case(readEnable, dataIn, readRow, dataOut) => {
      poke(c.io.readEnable, readEnable)
      poke(c.io.dataIn, dataIn)
      poke(c.io.rowSelect, readRow)
      expect(c.io.dataOut, dataOut)
      step(1)
    }
  }

}


class daisyVecMatTest(c: daisyVecMat, inputs: List[(Int,Int,Int,Int,Option[Int],Int,Int)]) extends PeekPokeTester(c) {

  (inputs).foreach {
    case(dataInA, readEnableA, dataInB, readEnableB, dataOutExpect, dataValidExpect, doneExpect) => {
      poke(c.io.dataInA, dataInA)
      poke(c.io.dataInB, dataInB)
      poke(c.io.readEnableA, readEnableA)
      poke(c.io.readEnableB, readEnableB)
      expect(c.io.dataValid, dataValidExpect)
      expect(c.io.done, doneExpect)
      dataOutExpect.foreach { expect(c.io.dataOut, _) }
      step(1)
    }
  }

}


class daisyVecSpec extends FlatSpec with Matchers {

  val input1 = List.fill(10)((0, 0x45, 0))

  val input2 = input1 ++ List(
    // enableIn, dataIn, expected
      (1,        2,      0),
      (1,        2,      0),
      (1,        2,      0),
      (1,        2,      0),
      (0,        0,      2),
      (0,        0,      2),
      (0,        0,      2),
      (0,        0,      2),
      (0,        0,      2),
      (0,        0,      2),
      (0,        0,      2),
      (0,        0,      2))


  val input3 = {
    val inputs = List.fill(100)(scala.util.Random.nextInt(10000))
    val withExpected = (List.fill(4)(0) ++ inputs) zip inputs
    val withEnabled = withExpected.map{ case(expected, in) => (1, in, expected) }

    withEnabled
  }

  behavior of "daisy vector"

  it should "not read when read enable is low" in {
    iotesters.Driver.execute(() => new daisyVector(4, 32), new TesterOptionsManager) { c =>
      new daisyVectorTest(c, input1)
    } should be(true)
  }


  it should "read only when read enable is asserted" in {
    iotesters.Driver.execute(() => new daisyVector(4, 32), new TesterOptionsManager) { c =>
      new daisyVectorTest(c, input2)
    } should be(true)
  }


  it should "Work in general" in {
    iotesters.Driver.execute(() => new daisyVector(4, 32), new TesterOptionsManager) { c =>
      new daisyVectorTest(c, input3)
    } should be(true)
  }
}


class daisyDotSpec extends FlatSpec with Matchers {
  behavior of "daisy vector"

  val input1 = List(
    (0, 0, None, 0),
    (0, 0, None, 0),
    (0, 0, None, 1),
    (0, 0, None, 0),
    (0, 0, None, 0),
    (0, 0, None, 1),
    (0, 0, None, 0),
    (0, 0, None, 0),
    (0, 0, None, 1))

  it should "Only signal valid output at end of calculation" in {
    iotesters.Driver.execute(() => new daisyDot(3, 32), new TesterOptionsManager) { c =>
      new daisyDotTest(c, input1)
    } should be(true)
  }


  val input2 = List(
    (1, 0, None, 0),
    (1, 0, None, 0),
    (1, 0, Some(3), 1),
    (1, 0, None, 0),
    (1, 0, None, 0),
    (1, 0, Some(3), 1),
    (1, 0, None, 0),
    (1, 0, None, 0),
    (1, 0, Some(3), 1))

  it should "Be able to count to 3" in {
    iotesters.Driver.execute(() => new daisyDot(3, 32), new TesterOptionsManager) { c =>
      new daisyDotTest(c, input1)
    } should be(true)
  }

  def createProblem(vecLen: Int): List[(Int, Int, Option[Int], Int)] = {
    val in1 = List.fill(vecLen)(scala.util.Random.nextInt(10))
    val in2 = List.fill(vecLen)(scala.util.Random.nextInt(10))

    val dotProduct = (in1, in2).zipped.map(_*_).sum

    (in1, in2, (0 to vecLen)).zipped.map{
      case(a, b, idx) =>
        val dpExpect  = if(idx == (vecLen - 1)) Some(dotProduct) else None
        val outExpect = if(idx == (vecLen - 1)) 1 else 0

        (a, b, dpExpect, outExpect)
    }
  }


  def createProblems(vecLen: Int): List[(Int, Int, Option[Int], Int)] =
    List.fill(10)(createProblem(vecLen)).flatten


  it should "Be able to calculate dot products" in {
    iotesters.Driver.execute(() => new daisyDot(10, 32), new TesterOptionsManager) { c =>
      new daisyDotTest(c, createProblems(10))
    } should be(true)
  }
}


class daisyGridSpec extends FlatSpec with Matchers {
  type Matrix[A] = List[List[A]]

  behavior of "daisy grid"

  def genMatrix(dims: (Int,Int)): Matrix[Int] =
    List.fill(dims._1)(
      List.fill(dims._2)(scala.util.Random.nextInt(100))
    )

  def readRowCheck(dims: (Int,Int)): List[(Int,Int,Int,Int)] = {
    //                    readEn, dataIn, readRow, expected dataOut
    List.fill(dims._1 - 1)((  1,      1,      0,       0)) ++
      List.fill(dims._1 - 1)((0,      0,      0,       1)) ++
      List.fill(dims._1 - 1)((0,      0,      0,       1))
  }

  def readRow2Check(dims: (Int,Int)): List[(Int,Int,Int,Int)] = {
    //                    readEn, dataIn, readRow, expected dataOut
    List.fill(dims._1 - 1)((  1,      1,      1,       0)) ++
      List.fill(dims._1 - 1)((0,      0,      1,       1)) ++
      List.fill(dims._1 - 1)((0,      0,      1,       1))
  }

  def readMatrix(dims: (Int,Int)): List[(Int,Int,Int,Int)] = {
    val m = genMatrix(dims)
    val input = m.zipWithIndex.map{ case(row, rowIdx) =>
      row.zipWithIndex.map{ case(a, colIdx) =>
        // readEn, dataIn, readRow, expected dataOut
        (  1,      a,      rowIdx,  0)
      }
    }.flatten

    val output = m.zipWithIndex.map{ case(row, rowIdx) =>
      row.zipWithIndex.map{ case(a, colIdx) =>
        // readEn, dataIn, readRow, expected dataOut
        (  0,      0,      rowIdx,  a)
      }
    }.flatten

    input ++ output
  }


  it should "work like a regular daisyVec when row select is fixed to 0" in {
    iotesters.Driver.execute(() => new daisyGrid(5, 4, 32), new TesterOptionsManager) { c =>
      new daisyGridTest(c, readRowCheck((5,4)))
    } should be(true)
  }


  it should "work like a regular daisyVec when row select is fixed to 1" in {
    iotesters.Driver.execute(() => new daisyGrid(5, 4, 32), new TesterOptionsManager) { c =>
      new daisyGridTest(c, readRow2Check((5,4)))
    } should be(true)
  }


  it should "be able to read a matrix" in {
    iotesters.Driver.execute(() => new daisyGrid(5, 4, 32), new TesterOptionsManager) { c =>
      new daisyGridTest(c, readMatrix((5,4)))
    } should be(true)
  }
}


class daisyVecMatSpec extends FlatSpec with Matchers {
  type Matrix[A] = List[List[A]]
  def genMatrix(dims: (Int,Int)): Matrix[Int] =
    List.fill(dims._1)(
      List.fill(dims._2)(scala.util.Random.nextInt(4))
    )


  def generateInputs(dims: (Int,Int)): List[(Int,Int,Int,Int,Option[Int],Int,Int)] = {

    val matrixB = genMatrix(dims)
    val vecA = genMatrix((1, (dims._1))).head

    println("multiplying: ")
    println(vecA.mkString("[","\t","]"))
    println("matrix:")
    matrixB.foreach { row =>
      println(row.mkString("[","\t","]"))
    }

    def answers: List[Int] = matrixB.transpose.map( col =>
      (col, vecA).zipped.map(_*_).sum
    )

    println("should equal")
    println(answers.mkString("[","\t","]"))


    val vecAndMatrixInput = (matrixB.head zip vecA).map{
      case(m, v) =>
        (v, 1, m, 1, None, 0, 0)
    }

    val matrixInput = matrixB.tail.flatten.map{ m =>
      (0, 0, m, 1, None, 0, 0)
    }

    val checkOutput = answers.map( a =>
      {
        val filler = List.fill(dims._2 - 1)((0, 0, 0, 0, None, 0, 0))
        val check = List((0, 0, 0, 0, Some(a), 0, 0))
        filler ++ check
      }).flatten

    vecAndMatrixInput ::: matrixInput ::: checkOutput
  }



  behavior of "vec mat multiplier"

  it should "compile" in {
    iotesters.Driver.execute(() => new daisyVecMat(5, 4, 5, 32), new TesterOptionsManager) { c =>
      new daisyVecMatTest(c, Nil)
    } should be(true)
  }



  it should "Not assert valid output when loading data" in {
    iotesters.Driver.execute(() => new daisyVecMat(5, 4, 5, 32), new TesterOptionsManager) { c =>
      new daisyVecMatTest(c, generateInputs((5,4)))
    } should be(true)
  }
}
