package Core
import chisel3._
import chisel3.iotesters._
import org.scalatest.{Matchers, FlatSpec}
import testUtils._


class daisyDotSpec extends FlatSpec with Matchers {

  behavior of "daisy vector"


  it should "Only signal valid output at end of calculation" in {

    val ins = (0 to 20).map(ii =>
      CycleTask[daisyDot](
        ii,
        d => d.poke(d.dut.io.dataInA, 0),
        d => d.poke(d.dut.io.dataInB, 0),
        d => d.expect(d.dut.io.outputValid, if((ii % 3) == 2) 1 else 0),
        )
    )

    iotesters.Driver.execute(() => new daisyDot(3, 32), new TesterOptionsManager) { c =>
      IoSpec[daisyDot](ins, c).myTester
    } should be(true)
  }

  it should "Be able to count to 3" in {

    val ins = (0 to 20).map(ii =>
      CycleTask[daisyDot](
        ii,
        d => d.poke(d.dut.io.dataInA, 1),
        d => d.poke(d.dut.io.dataInB, 1),
        d => d.expect(d.dut.io.outputValid, if((ii % 3) == 2) 1 else 0),
        d => if(d.peek(d.dut.io.outputValid) == 1)
               d.expect(d.dut.io.dataOut, 3)
      )
    )

    iotesters.Driver.execute(() => new daisyDot(3, 32), new TesterOptionsManager) { c =>
      IoSpec[daisyDot](ins, c).myTester
    } should be(true)
  }



  it should "Be able to calculate dot products" in {

    def createProblem(vecLen: Int): List[CycleTask[daisyDot]] = {

      val in1 = List.fill(vecLen)(scala.util.Random.nextInt(10))
      val in2 = List.fill(vecLen)(scala.util.Random.nextInt(10))

      val dotProduct = (in1, in2).zipped.map(_*_).sum

      (in1, in2, (0 to vecLen)).zipped.map{
        case(a, b, idx) =>
          CycleTask[daisyDot](
            idx,
            d => d.poke(d.dut.io.dataInA, a),
            d => d.poke(d.dut.io.dataInB, b),
            d => if(d.peek(d.dut.io.outputValid) == 1)
                   d.expect(d.dut.io.dataOut, dotProduct)
          )
      }
    }


    def createProblems(vecLen: Int): List[CycleTask[daisyDot]] =
      List.fill(10)(createProblem(vecLen)).zipWithIndex.map{ case(probs, idx) =>
        probs.map(_.delay(3*idx))
      }.flatten



    iotesters.Driver.execute(() => new daisyDot(3, 32), new TesterOptionsManager) { c =>
      IoSpec[daisyDot](createProblems(3), c).myTester
    } should be(true)
  }
}
