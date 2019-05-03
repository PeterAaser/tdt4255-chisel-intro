/**
  * This code supplements instructions.org
  */
package Examples
import Ex0._


import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._


class PrintfExampleSpec extends FlatSpec with Matchers {

  class PrintfExample() extends Module {
    val io = IO(new Bundle{})
    
    val counter = RegInit(0.U(8.W))
    counter := counter + 1.U
  
    printf("Counter is %d\n", counter)
    when(counter % 2.U === 0.U){
      printf("Counter is even\n")
    }
  }

  class PrintfTest(c: PrintfExample) extends PeekPokeTester(c)  {
    for(ii <- 0 until 5){
      println(s"At cycle $ii:")
      step(1)
    }
  }


  behavior of "Printf Example"

  it should "print" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new PrintfExample) { c =>
        new PrintfTest(c)
      } should be(true)
    )
  }
}


class EvilPrintfSpec extends FlatSpec with Matchers {

  class CountTo3() extends Module {
    val io = IO(
      new Bundle {
        val dataOut     = Output(UInt(32.W))
        val validOutput = Output(Bool())
      }
    )
    val count = RegInit(UInt(32.W), 0.U)
    io.dataOut := count

    printf(p"according to printf output is: ${io.dataOut}\n")

    when(count != 3.U){
      count := count + 1.U
      io.validOutput := false.B
      io.dataOut := 0.U
    }.otherwise{
      io.validOutput := true.B
      io.dataOut := 1.U
    }

  }


  class CountTo3Test(c: CountTo3) extends PeekPokeTester(c)  {
    for(ii <- 0 until 5){
      println(s"\nIn cycle $ii the output of counter is: ${peek(c.io.dataOut)}")
      step(1)
    }
  }

  behavior of "EvilPrintf"

  it should "tell a lie and hurt you" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new CountTo3, "verilator") { c =>
        new CountTo3Test(c)
      } should be(true)
    )
  }
}


class PeekInternalSpec extends FlatSpec with Matchers {

  class Inner() extends Module {
    val io = IO(
      new Bundle {
        val dataIn  = Input(UInt(32.W))
        val dataOut = Output(UInt(32.W))
      }
    )
    val innerState = RegInit(0.U)
    when(io.dataIn % 2.U === 0.U){
      innerState := io.dataIn
    }

    io.dataOut := innerState
  }


  class Outer() extends Module {
    val io = IO(
      new Bundle {
        val dataIn  = Input(UInt(32.W))
        val dataOut = Output(UInt(32.W))
      }
    )
    
    val outerState = RegInit(0.U)
    val inner = Module(new Inner)
    
    outerState      := io.dataIn
    inner.io.dataIn := outerState
    io.dataOut      := inner.io.dataOut
  }

  class OuterTester(c: Outer) extends PeekPokeTester(c)  {
    val inner = peek(c.inner.innerState)
    val outer = peek(c.outerState)
  }

  behavior of "peek poke internal"

  it should "Throw an exception" in {
    val success = try {
      chisel3.iotesters.Driver(() => new Outer) { c =>
        new OuterTester(c)
      } should be(true)
    }
    catch {
      case e: java.util.NoSuchElementException => true
      case e: Throwable => throw e
    }
  }
}


/**
  * Inner state has been exposed manually
  * 
  * This creates a lot of extra signals in the IO module, and it's a hassle to do the wiring.
  */
class PeekInternalExposedSpec extends FlatSpec with Matchers {

  class Inner() extends Module {
    val io = IO(
      new Bundle {
        val dataIn  = Input(UInt(32.W))
        val dataOut = Output(UInt(32.W))
        val stateDebug = Output(UInt(32.W))
      }
    )
    val innerState = RegInit(0.U)
    innerState := io.dataIn

    when(innerState % 2.U === 0.U){
      io.dataOut := io.dataIn
    }.otherwise{
      io.dataOut := innerState
    }

    io.stateDebug := innerState
  }


  class Outer() extends Module {
    val io = IO(
      new Bundle {
        val dataIn  = Input(UInt(32.W))
        val dataOut = Output(UInt(32.W))

        val innerStateDebug = Output(UInt(32.W))
        val outerStateDebug = Output(UInt(32.W))
      }
    )
    
    val outerState = RegInit(0.U)
    val inner = Module(new Inner)
    
    outerState      := io.dataIn
    inner.io.dataIn := outerState
    io.dataOut      := inner.io.dataOut

    io.innerStateDebug := inner.io.stateDebug
    io.outerStateDebug := outerState
  }

  class OuterTester(c: Outer) extends PeekPokeTester(c)  {
    for(ii <- 0 until 10){
      poke(c.io.dataIn, ii)
      val inner = peek(c.io.innerStateDebug)
      val outer = peek(c.io.outerStateDebug)
      println(s"$inner")
      println(s"$outer")
      println()
      step(1)
    }
  }

  behavior of "peek poke internal"

  it should "peek exposed signals" in {
    chisel3.iotesters.Driver(() => new Outer) { c =>
      new OuterTester(c)
    } should be(true)
  }
}
