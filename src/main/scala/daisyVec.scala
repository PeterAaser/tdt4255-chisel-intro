package Core
import chisel3._
import chisel3.core.Input
import chisel3.iotesters.PeekPokeTester

class daisyVector(elements: Int, dataWidth: Int) extends Module{

  val io = IO(new Bundle {
    val readEnable = Input(Bool())
    val dataIn     = Input(UInt(dataWidth.W))

    val dataOut    = Output(UInt(dataWidth.W))
  })

  val currentIndex = RegInit(UInt(8.W), 0.U)

  val memory = Array.fill(elements)(Reg(UInt(dataWidth.W)))

  when(currentIndex === (elements - 1).U ){
    currentIndex := 0.U
  }.otherwise{
    currentIndex := currentIndex + 1.U
  }


  io.dataOut := 0.U

  for(ii <- 0 until elements){
    when(currentIndex === ii.U){
      when(io.readEnable === true.B){
        memory(ii) := io.dataIn
      }
      io.dataOut := memory(ii)
    }
  }
}

class daisyVectorTest(c: daisyVector) extends PeekPokeTester(c) {

  poke(c.io.readEnable, 1)
  step(1)

  for(ii <- 0 until 4){
    poke(c.io.dataIn, ii)
    println("////////////////////")
    step(1)
  }

  poke(c.io.readEnable, 0)
  for(ii <- 0 until 4){
    peek(c.io.dataOut)
    step(1)
  }
}
