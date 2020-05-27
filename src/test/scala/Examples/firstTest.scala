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
import FileUtils._


/**
  Any class that implements FlatSpec and Matchers is accessible as a test.
  This file is part of the package Examples (as defined on the first line of code).
  The "name" of the test is therefore "Examples.FirstTest" and can be run with
  "testOnly Examples.FirstTest" in your sbt console.
  */
class FirstTest extends org.scalatest.FlatSpec with org.scalatest.Matchers {

  behavior of "my first module"
  it should "Help you understand testing in scala and chisel!" in {

    warn("Test starting:")

    chisel3.iotesters.Driver{
      warn("synthesizing module");
      () => new MyModule(3)}
    { c =>
      warn("MyModule has been synthesized successfully!")
      new TestRunner(c)
    } should be(true)


    warn("Test Finished")
  }
}


// The module we want to test
class MyModule(val incrementBy: Int) extends chisel3.Module {
  val io = IO(
    new Bundle {
      val dataIn  = Input(UInt(32.W))
      val dataOut = Output(UInt(32.W))
    }
  )

  /**
    This is the body of MyModule
    */

  io.dataOut := 0.U

  // The commented code is supplied so that you should have an idea of what you should
  // end up with when you're through the introductionary section.
  // It's purpose is to ensure that you understand where a certain piece of code from the
  // introduction actually goes which might not be entirely obvious.

  // Increment input by some value
  // io.dataOut := io.dataIn + incrementBy.U


  // A basic stateful circuit. It ignores input!
  // val regA = RegInit(2.U(4.W))
  // val regB = RegInit(1.U(4.W))
  // regA := regB
  // regB := regA
  // io.dataOut := regA
}


// The tests we want to run on the module
class TestRunner(c: MyModule) extends chisel3.iotesters.PeekPokeTester(c)  {
  warn("testRunner executing...")

  /**
    This is the body of the TestRunner
    */

  // The commented code is supplied so that you should have an idea of what you should
  // end up with when you're through the introductionary section.
  // It's purpose is to ensure that you understand where a certain piece of code from the
  // introduction actually goes which might not be entirely obvious.

  // calling peek with super just to emphasize where peek and poke comes from,
  // and why we can't call it directly on the module argument c
  // val o = super.peek(c.io.dataOut)
  // say(s"observed state: $o")
  // poke(c.io.dataIn, 3)
  // val o2 = super.peek(c.io.dataOut)
  // say(s"observed state after poking: $o")


  // for(ii <- 0 until 10){
  //   poke(c.io.dataIn, ii.U)
  //   val o3 = peek(c.io.dataOut)
  //   say(s"observed state at iteration $ii: $o3")
  //   step(1)
  // }

  warn("testRunner finished...")
}
