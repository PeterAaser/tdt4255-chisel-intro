package Core
import chisel3._
import chisel3.iotesters._
import org.scalatest.{Matchers, FlatSpec}
import testUtils._


class daisyVecSpec extends FlatSpec with Matchers {

  behavior of "daisy vector"

  it should "not write when write enable is low" in {

    val ins = (0 to 10).map(ii =>
      CycleTask[daisyVector](
        ii,
        d => d.poke(d.dut.io.dataIn, 0),
        d => d.poke(d.dut.io.writeEnable, 0),
        d => d.expect(d.dut.io.dataOut, 0))
    ).toList


    iotesters.Driver.execute(() => new daisyVector(4, 32), new TesterOptionsManager) { c =>
      IoSpec[daisyVector](ins, c).myTester
    } should be(true)
  }



  it should "write only when write enable is asserted" in {

    val ins =
      (0 until 4).map(ii =>
        CycleTask[daisyVector](
          ii,
          _ => println("inputting 2s'"),
          d => d.poke(d.dut.io.dataIn, 2),
          d => d.poke(d.dut.io.writeEnable, 1))) ++
      (0 until 6).map(ii =>
        CycleTask[daisyVector](
          ii + 4,
          _ => println("Checking output is 2"),
          d => d.poke(d.dut.io.writeEnable, 0),
          d => d.expect(d.dut.io.dataOut, 2)
    ))

    iotesters.Driver.execute(() => new daisyVector(4, 32), new TesterOptionsManager) { c =>
      IoSpec[daisyVector](ins, c).myTester
    } should be(true)
  }



  it should "Work in general" in {


    val ins = {
      val inputs = List.fill(10)(scala.util.Random.nextInt(10000))

      println(inputs)

      val in = inputs.zipWithIndex.map{ case(in,idx) =>
        CycleTask[daisyVector](
          idx,
          d => d.poke(d.dut.io.dataIn, in),
          d => d.poke(d.dut.io.writeEnable, 1)
        )
      }

      val out = inputs.zipWithIndex.map{ case(expected, idx) =>
        CycleTask[daisyVector](
          idx + 4,
          d => d.expect(d.dut.io.dataOut, expected)
        )
      }

      in ::: out
    }

    iotesters.Driver.execute(() => new daisyVector(4, 32), new TesterOptionsManager) { c =>
      IoSpec[daisyVector](ins, c).myTester
    } should be(true)
  }
}
