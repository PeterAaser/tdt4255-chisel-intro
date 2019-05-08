package Ex0

import chisel3._
import chisel3.experimental._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._

import scala.collection.immutable.{ Vector => _ }

class SVGSpec extends FlatSpec with Matchers {
  import AdderTests._

  behavior of "Adder"

  it should "Make some sweet pngs" in {
    // FileUtils.getSvg("Adder")
    wrapTester(
      chisel3.iotesters.Driver(() => new Adder()) { c =>
        new AdderTester(c)
      } should be(true)
    )
  }

}

class Adder() extends Module {
  val io = IO(
    new Bundle {
      val reg_a = Output(UInt(32.W))
    }
  )

  val reg_a = RegInit(0.U(8.W))
  reg_a := reg_a + 1.U

  io.reg_a := reg_a
}

object AdderTests {
  
  class AdderTester(c: Adder) extends PeekPokeTesterLogger(c)  {
    override def ioLoggers = List(c.io)

    for(ii <- 0 until 10){
      step(1)
    }

    writeLog
  }
}
