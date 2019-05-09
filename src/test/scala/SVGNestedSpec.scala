package Ex0

import chisel3._
import chisel3.experimental._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._

import scala.collection.immutable.{ Vector => _ }

class SVGSNestedSpec extends FlatSpec with Matchers {

  behavior of "SumOrSquare"

  it should "Make some sweet pngs" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new SumOrSquare(5, 7)) { c =>
        new SumOrSquareTester(c)
      } should be(true)
    )
  }

}

class MyCounter(countTo: Int) extends MultiIOModule {
  val io = IO( new Bundle {
    val out = Output(UInt(32.W))
  })

  val debug = IO( new Bundle {
    val counterState = Output(UInt(32.W))
  })

  val reg_a = RegInit(0.U(8.W))
  val incremented = reg_a + 1.U

  when(incremented === countTo.U){
    reg_a := 0.U
  }.otherwise{
    reg_a := reg_a + 1.U
  }

  io.out := incremented

  debug.counterState := reg_a
}


class SumOrSquare(countToA: Int, countToB: Int) extends MultiIOModule {
  val io = IO( new Bundle {
    val out = Output(UInt(32.W))
  })


  val debug = IO( new Bundle {
    val counter_a = Output(UInt(32.W))
    val counter_b = Output(UInt(32.W))
    val square    = Output(UInt(32.W))
    val sum       = Output(UInt(32.W))
  })


  val counterA = Module(new MyCounter(countToA))
  val counterB = Module(new MyCounter(countToB))

  val sum = counterA.io.out + counterA.io.out
  val square = counterA.io.out * counterA.io.out

  when(counterB.io.out % 2.U === 0.U){
    io.out := sum
  }.otherwise{
    io.out := square
  }


  debug.counter_a := counterA.debug.counterState
  debug.counter_b := counterB.debug.counterState
  debug.square    := square
  debug.sum       := sum
}


class SumOrSquareTester(c: SumOrSquare) extends PeekPokeTesterLogger(c)  {
  override def ioLoggers = List(
    "" -> c.debug,
    "" -> c.io
  )

  for(ii <- 0 until 10){
    step(1)
  }

  writeLog
}

