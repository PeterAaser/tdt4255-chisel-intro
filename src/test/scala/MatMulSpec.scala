package Ex0

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._

class MatMulSpec extends FlatSpec with Matchers {
  import MatMulTests._

  val rowDims = 3
  val colDims = 7


  behavior of "MatMul"

  it should "Multiply two matrices" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new MatMul(rowDims, colDims)) { c =>
        new FullMatMul(c)
      } should be(true)
    )
  }
}

object MatMulTests {

  val rand = new scala.util.Random(100)

  class TestExample(c: MatMul) extends PeekPokeTester(c) {
    val mA = genMatrix(c.rowDimsA, c.colDimsA)
    val mB = genMatrix(c.rowDimsA, c.colDimsA)
    val mC = matrixMultiply(mA, mB.transpose)


  }

  class FullMatMul(c: MatMul) extends PeekPokeTester(c) {

    val mA = genMatrix(c.rowDimsA, c.colDimsA)
    val mB = genMatrix(c.rowDimsA, c.colDimsA)
    val mC = matrixMultiply(mA, mB.transpose)

    println("Multiplying")
    println(printMatrix(mA))
    println("With")
    println(printMatrix(mB.transpose))
    println("Expecting")
    println(printMatrix(mC))

    // Input data
    for(ii <- 0 until c.colDimsA * c.rowDimsA){

      val rowInputIdx = ii / c.colDimsA
      val colInputIdx = ii % c.colDimsA

      poke(c.io.dataInA, mA(rowInputIdx)(colInputIdx))
      poke(c.io.dataInB, mB(rowInputIdx)(colInputIdx))
      expect(c.io.outputValid, false, "Valid output during initialization")

      step(1)
    }

    // Perform calculation
    for(ii <- 0 until (c.rowDimsA * c.rowDimsA)){
      for(kk <- 0 until c.colDimsA - 1){
        expect(c.io.outputValid, false, "Valid output mistimed")
        step(1)
      }
      expect(c.io.outputValid, true, "Valid output timing is wrong")
      expect(c.io.dataOut, mC(ii / c.rowDimsA)(ii % c.rowDimsA), "Wrong value calculated")
      step(1)
    }
  }
}
