/**
  * This code supplements instructions.org
  * Once you've gone through the instructions you can do
  * whatever you want with it.
  */
package Examples
import Ex0._


import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._


class MyIncrementTest extends FlatSpec with Matchers {

  class MyIncrement(val incrementBy: Int) extends Module {
    val io = IO(
      new Bundle {
        val dataIn  = Input(UInt(32.W))
        val dataOut = Output(UInt(32.W))
      }
    )

    io.dataOut := io.dataIn + incrementBy.U
  }

  class TheTestRunner(c: MyIncrement) extends PeekPokeTester(c)  {
    for(ii <- 0 until 5){
      poke(c.io.dataIn, ii)
      val o = peek(c.io.dataOut)
      println(s"At cycle $ii the output of myIncrement was $o")
      expect(c.io.dataOut, ii+c.incrementBy)
    }
  }

  behavior of "my increment"

  it should "increment its input by 3" in {
    chisel3.iotesters.Driver(() => new MyIncrement(3)) { c =>
      new TheTestRunner(c)
    } should be(true)
  }
}


class MyIncrementManyTest extends FlatSpec with Matchers {

  class MyIncrement(val incrementBy: Int) extends Module {
    val io = IO(
      new Bundle {
        val dataIn  = Input(UInt(32.W))
        val dataOut = Output(UInt(32.W))
      }
    )
    io.dataOut := io.dataIn + incrementBy.U
  }


  class MyIncrementN(val incrementBy: Int, val numIncrementors: Int) extends Module {
    val io = IO(
      new Bundle {
        val dataIn  = Input(UInt(32.W))
        val dataOut = Output(UInt(32.W))
      }
    )

    val incrementors = Array.fill(numIncrementors){ Module(new MyIncrement(incrementBy)) }

    for(ii <- 1 until numIncrementors){
      incrementors(ii).io.dataIn := incrementors(ii - 1).io.dataOut
    }

    incrementors(0).io.dataIn := io.dataIn
    io.dataOut := incrementors.last.io.dataOut
  }


  class TheTestRunner(c: MyIncrementN) extends PeekPokeTester(c)  {
    for(ii <- 0 until 5){
      poke(c.io.dataIn, ii)
      val o = peek(c.io.dataOut)
      println(s"At cycle $ii the output of myIncrement was $o")
      expect(c.io.dataOut, ii+(c.incrementBy*c.numIncrementors))
    }
  }

  behavior of "my incrementN"

  it should "increment its input by 3*4" in {
    chisel3.iotesters.Driver.execute(Array("--generate-vcd-output", "on", "--target-dir", ".", "--backend-name", "treadle"), () => new MyIncrementN(3,4)) { c =>
    // chisel3.iotesters.Driver(() => new MyIncrementN(4, 3)) { c =>
      new TheTestRunner(c)
    } should be(true)
  }
}



class ChiselConditional() extends Module {
  val io = IO(
    new Bundle {
      val a = Input(UInt(32.W))
      val b = Input(UInt(32.W))
      val opSel = Input(Bool())

      val out = Output(UInt(32.W))
    }
  )

  when(io.opSel){
    io.out := io.a + io.b
  }.otherwise{
    io.out := io.a - io.b
  }
}



class ScalaConditional(opSel: Boolean) extends Module {
  val io = IO(
    new Bundle {
      val a = Input(UInt(32.W))
      val b = Input(UInt(32.W))

      val out = Output(UInt(32.W))
    }
  )

  if(opSel){
    io.out := io.a + io.b
  } else {
    io.out := io.a - io.b
  }
}
