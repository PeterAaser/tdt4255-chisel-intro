/**
  * This code supplements instructions.org
  * Once you've gone through the instructions you can do
  * whatever you want with it.
  */
package Ex0

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._

// class MyVector() extends Module {
//   val io = IO(
//     new Bundle {
//       val idx = Input(UInt(32.W))
//       val out = Output(UInt(32.W))
//     }
//   )

//   val values = List(1, 2, 3, 4)

//   io.out := values(io.idx)
// }

// class MyVector() extends Module {
//   val io = IO(
//     new Bundle {
//       val idx = Input(UInt(32.W))
//       val out = Output(UInt(32.W))
//     }
//   )

//   // val values: List[Int] = List(1, 2, 3, 4)
//   val values = Vec(1, 2, 3, 4)

//   io.out := values(io.idx)
// }

class MyVector() extends Module {
  val io = IO(
    new Bundle {
      val idx = Input(UInt(32.W))
      val out = Output(UInt(32.W))
    }
  )

  val values = Vec(0.U, 1.U, 2.U, 3.U)

  io.out := values(io.idx)
}


class MyVector2() extends Module {
  val io = IO(
    new Bundle {
      val idx = Input(UInt(2.W))
      val out = Output(UInt(32.W))
    }
  )

  val values = Array(0.U, 1.U, 2.U, 3.U)

  val myWire = Wire(UInt(4.W))
  io.out := values(0)
  for(ii <- 0 until 4){
    when(io.idx === ii.U){
      io.out := values(ii)
    }
  }
}


class MyVecSpec extends FlatSpec with Matchers {
  behavior of "MyVec"

  it should "Output whatever idx points to" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new MyVector2) { c =>
        new MyVecTester(c)
      } should be(true)
    )
  }
}


class MyVecTester(c: MyVector2) extends PeekPokeTester(c)  {
  for(ii <- 0 until 4){
    poke(c.io.idx, ii)
    expect(c.io.out, ii)
  }
}


class Invalid() extends Module {
  val io = IO(new Bundle{})

  val myVec = Module(new MyVector)

  // Uncomment line below to make the circuit valid
  // myVec.io.idx := 0.U
}


/**
  * This goes a little beyond the example in exercise.org.
  * WrapTest is a simple wrapper that catches Unconnected wires
  * and prints them with a less scary stacktrace.
  * Additionally, we throw a RunTimeException instead of ??? for
  * similar reasons
  * 
  */
class InvalidSpec extends FlatSpec with Matchers {
  behavior of "Invalid"

  it should "Fail with a RefNotInitializedException" in {
    try {
      wrapTester(
        chisel3.iotesters.Driver(() => new Invalid) { c =>

          // Just a placeholder so it compiles
          throw new RuntimeException with scala.util.control.NoStackTrace
        } should be(true)
      )
    }
    catch {
      case e: RuntimeException => println("all good!")
      case e: Exception => throw e
    }
  }
}


class SimpleDelay() extends Module {
  val io = IO(
    new Bundle {
      val dataIn  = Input(UInt(32.W))
      val dataOut = Output(UInt(32.W))
    }
  )
  val delayReg = RegInit(UInt(32.W), 0.U)

  delayReg   := io.dataIn
  io.dataOut := delayReg
}


class DelaySpec extends FlatSpec with Matchers {
  behavior of "SimpleDelay"

  it should "Delay input by one timestep" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new SimpleDelay) { c =>
        new DelayTester(c)
      } should be(true)
    )
  }
}


// class DelayTester(c: SimpleDelay) extends PeekPokeTester(c)  {
//   for(ii <- 0 until 10){
//     val input = scala.util.Random.nextInt(10)
//     poke(c.io.dataIn, input)
//     expect(c.io.dataOut, input)
//   }
// }

class DelayTester(c: SimpleDelay) extends PeekPokeTester(c)  {
  for(ii <- 0 until 10){
    val input = scala.util.Random.nextInt(10)
    poke(c.io.dataIn, input)
    step(1)
    expect(c.io.dataOut, input)
  }
}
