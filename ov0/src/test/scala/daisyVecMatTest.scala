package Core
import chisel3._
import chisel3.iotesters._
import org.scalatest.{Matchers, FlatSpec}
import testUtils._
import utilz._


class daisyVecMatSpec extends FlatSpec with Matchers {

  def generateProblem(dims: Dims): List[CycleTask[daisyVecMat]] = {

    // for a vec len A, a matrix must have dims A rows
    val matrixB = genMatrix(dims).transpose
    val vecA = List.fill(dims.rows)(scala.util.Random.nextInt(5))

    def answers: List[Int] = matrixB.map( col =>
      (col, vecA).zipped.map(_*_).sum)

    println("multiplying vector: ")
    println(printVector(vecA))
    println("with matrix:")
    println(printMatrix(matrixB.transpose))
    println("which should equal")
    println(printVector(answers))

    println("Input order of matrix:")
    println(printMatrix(matrixB))


    val vecInput = vecA.zipWithIndex.map{
      case(in, idx) =>
        CycleTask[daisyVecMat](
          idx,
          d => d.poke(d.dut.io.dataInA, in),
          d => d.poke(d.dut.io.writeEnableA, 1)
        )
    }


    val matrixInput = matrixB.flatten.zipWithIndex.map{
      case(in, idx) =>
        CycleTask[daisyVecMat](
          idx,
          d => d.poke(d.dut.io.dataInB, in),
          d => d.poke(d.dut.io.writeEnableB, 1)
        )
    }


    val inputDisablers = List(
      CycleTask[daisyVecMat](
        dims.rows,
        d => d.poke(d.dut.io.writeEnableA, 0)
      ),
      CycleTask[daisyVecMat](
        dims.elements,
        d => d.poke(d.dut.io.writeEnableB, 0)
      )
    )


    val checkValid1 = (0 until dims.elements).map( n =>
      CycleTask[daisyVecMat](
        n,
        d => d.expect(d.dut.io.dataValid, 0, "data valid should not be asserted before data is ready")
      )
    ).toList




    val checkValid2 = (0 until dims.elements).map{ n =>

      val shouldBeValid = (n % dims.rows) == dims.rows - 1

      val whichOutput = answers( (n/dims.rows) )

      CycleTask[daisyVecMat](
        n,
        d => if(!shouldBeValid)
               d.expect(d.dut.io.dataValid, 0)
             else {
               d.expect(d.dut.io.dataValid, 1)
               d.expect(d.dut.io.dataOut, whichOutput)
             }

      ).delay(dims.elements)
    }.toList


    // adds a lot of annoying noise
    // val peekDebug = (0 until 20).map(n =>
    //   CycleTask[daisyVecMat](
    //     n,
    //     _ => println(s"at step $n"),
    //     d => println(printModuleIO(d.peek(d.dut.io))),
    //     _ => println(),
    //     )
    // ).toList

    vecInput ::: matrixInput ::: inputDisablers ::: checkValid1 ::: checkValid2 // ::: peekDebug
  }



  behavior of "vec mat multiplier"

  val dims = Dims(rows = 3, cols = 2)

  it should "work" in {
    iotesters.Driver.execute(() => new daisyVecMat(dims, 32), new TesterOptionsManager) { c =>
      IoSpec[daisyVecMat](generateProblem(Dims(rows = 3, cols = 2)), c).myTester
    } should be(true)
  }
}
