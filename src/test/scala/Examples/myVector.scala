/**
  * This code supplements instructions.org
  */
package Examples
import Ex0._


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
