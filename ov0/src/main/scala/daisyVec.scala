package Core
import chisel3._
import chisel3.core.Input
import chisel3.iotesters.PeekPokeTester

/**
  DaisyVectors are not indexed externally. They have no control inputs or outputs, only data.
  */
class daisyVector(elements: Int, dataWidth: Int) extends Module{

  val io = IO(new Bundle {
    val writeEnable = Input(Bool())
    val dataIn     = Input(UInt(dataWidth.W))

    val dataOut    = Output(UInt(dataWidth.W))
  })

  /**
    although the vector is not accessible by index externally, an internal index is necessary
    It is initialized to the value 0
   */
  val currentIndex = RegInit(UInt(8.W), 0.U)
  val memory = Array.fill(elements)(RegInit(UInt(dataWidth.W), 0.U))


  /**
    Your implementation here
    */
  // Cycle the currentIndex register, it should be equal to the current (cycle % elements)

  // Connect the selected output to io.dataOut
  // Connect writeEnable to the selected memory (selectable with memory(currentIndex))
  /**
    LF
    */


  when(currentIndex === (elements - 1).U ){
    currentIndex := 0.U
  }.otherwise{
    currentIndex := currentIndex + 1.U
  }


  io.dataOut := 0.U

  for(ii <- 0 until elements){
    when(currentIndex === ii.U){
      when(io.writeEnable === true.B){
        memory(ii) := io.dataIn
      }
      io.dataOut := memory(ii)
    }
  }
}
