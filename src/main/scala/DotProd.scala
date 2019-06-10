package Ex0

import chisel3._
import chisel3.util.Counter

class DotProd(val elements: Int) extends Module {

  val io = IO(
    new Bundle {
      val dataInA     = Input(UInt(32.W))
      val dataInB     = Input(UInt(32.W))

      val dataOut     = Output(UInt(32.W))
      val outputValid = Output(Bool())
    }
  )


  /**
    * Your code here
    */
  val counter = Counter(elements)
  val accumulator = RegInit(UInt(32.W), 0.U)

  // Please don't manually implement product!
  val product = io.dataInA * io.dataInB

  // placeholder
  io.dataOut := 0.U
  io.outputValid := false.B
}
