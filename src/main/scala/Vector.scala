package Ex0

import chisel3._


class Vector(val elements: Int) extends Module {

  val io = IO(
    new Bundle {
      val idx         = Input(UInt(32.W))
      val dataIn      = Input(UInt(32.W))
      val writeEnable = Input(Bool())

      val dataOut     = Output(UInt(32.W))
    }
  )

  // Creates a vector of zero-initialized registers
  val internalVector = RegInit(VecInit(List.fill(elements)(0.U(32.W))))


  when(writeEnable){
    // TODO:
    // When writeEnable is true the content of internalVector at the index specified
    // by idx should be set to the value of io.dataIn
  }
  // In this case we don't want an otherwise block, in writeEnable is low we don't change
  // anything


  // TODO:
  // io.dataOut should be driven by the contents of internalVector at the index specified
  // by idx
  io.dataOut := 0.U
}
