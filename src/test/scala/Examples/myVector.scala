/**
  * This code supplements instructions.org
  */
package Examples
import Ex0._


import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._

// Does not compile
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


// Does not compile
// class MyVector() extends Module {
//   val io = IO(
//     new Bundle {
//       val idx = Input(UInt(32.W))
//       val out = Output(UInt(32.W))
//     }
//   )

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

  val values = chisel3.Vec(0.U, 1.U, 2.U, 3.U)

  io.out := values(io.idx)
}


class MyVecSpec extends FlatSpec with Matchers {

  class MyVecTester(c: MyVector) extends PeekPokeTester(c)  {
    for(ii <- 0 until 4){
      poke(c.io.idx, ii)
      expect(c.io.out, ii)
    }
  }

  behavior of "MyVec"

  it should "Output whatever idx points to" in {
    chisel3.iotesters.Driver(() => new MyVector) { c =>
      new MyVecTester(c)
    } should be(true)
  }
}





class MyVectorAlt() extends Module {
  val io = IO(
    new Bundle {
      val idx = Input(UInt(32.W))
      val out = Output(UInt(32.W))
    }
  )

  val values = Array(0.U, 1.U, 2.U, 3.U)

  io.out := values(0)
  for(ii <- 0 until 4){
    when(io.idx(1, 0) === ii.U){
      io.out := values(ii)
    }
  }
}


class MyVecAltSpec extends FlatSpec with Matchers {

  class MyVecTester(c: MyVectorAlt) extends PeekPokeTester(c)  {
    for(ii <- 0 until 4){
      poke(c.io.idx, ii)
      expect(c.io.out, ii)
    }
  }

  behavior of "MyVec"

  it should "Output whatever idx points to" in {
    chisel3.iotesters.Driver(() => new MyVectorAlt) { c =>
      new MyVecTester(c)
    } should be(true)
  }
}
