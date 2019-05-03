/**
  * This code supplements instructions.org
  */
package Examples
import Ex0._


import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._

class Invalid() extends Module {
  val io = IO(new Bundle{})

  val myVec = Module(new MyVector)

  // Uncomment line below to make the circuit valid
  // myVec.io.idx := 0.U
}

class InvalidSpec extends FlatSpec with Matchers {
  behavior of "Invalid"

  it should "fail" in {
    chisel3.iotesters.Driver(() => new Invalid) { c =>

      // chisel tester expects a test here, but we can use ???
      // which is shorthand for throw new NotImplementedException.
      //
      // This is OK, because it will fail during building.
      ???
    } should be(true)
  }
}


/**
  * Chisel errors are just regular scala Exceptions that can be caught.
  * The test underneath shows this in practice, and is used in the tests for 
  * the exercises via the wrapTester method.
  */
// class InvalidSpec extends FlatSpec with Matchers {
//   behavior of "Invalid"

//   it should "Fail with a RefNotInitializedException" in {
//     val passes = try {
//       chisel3.iotesters.Driver(() => new Invalid) { c =>
//         ???
//       }
//     }
//     catch {
//       case e: firrtl.passes.CheckInitialization.RefNotInitializedException => true
//       case _: Throwable => false
//     } 
//     passes should be(true)
//   }
// }
