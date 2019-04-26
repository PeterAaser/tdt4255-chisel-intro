package Ex0

import chisel3._


class Vector(val elements: Int) extends Module {

  val io = IO(
    new Bundle {
      val idx        = Input(UInt(32.W))
      val dataIn     = Input(UInt(32.W))
      val readEnable = Input(Bool())

      val dataOut    = Output(UInt(32.W))
    }
  )

  /**
    * Your code here
    */

  // Creates a vector of zero-initialized registers
  val contents = RegInit(VecInit(List.fill(elements)(0.U(32.W))))

  // placeholder
  io.dataOut := 0.U
}
