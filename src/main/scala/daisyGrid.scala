package Core
import chisel3._
import chisel3.core.Input
import chisel3.iotesters.PeekPokeTester

class daisyGrid(rows: Int, cols: Int, dataWidth: Int) extends Module{

  val io = IO(new Bundle {

    val readEnable = Input(Bool())
    val dataIn     = Input(UInt(dataWidth.W))
    val readRow    = Input(UInt(8.W))
    // val reset      = Input(Bool())

    val dataOut    = Output(UInt(dataWidth.W))
  })

  val currentRowIndex = RegInit(UInt(8.W), 0.U)
  val currentColIndex = RegInit(UInt(8.W), 0.U)

  val memRows = Array.fill(rows){ Module(new daisyVector(cols, dataWidth)).io }
  val elements = rows*cols


  io.dataOut := 0.U

  for(ii <- 0 until rows){

    memRows(ii).readEnable := 0.U
    memRows(ii).dataIn := io.dataIn

    when(io.readRow === ii.U ){
      memRows(ii).readEnable := io.readEnable
      io.dataOut := memRows(ii).dataOut
    }
  }
}

class daisyGridTest(c: daisyGrid) extends PeekPokeTester(c) {

  poke(c.io.readEnable, 1)
  for(ii <- 0 until 12){
    poke(c.io.dataIn, ii)
    poke(c.io.readRow, ii/3)
    step(1)
    println("////////////////////")
  }
  poke(c.io.readEnable, 0)
  for(ii <- 0 until 12){
    peek(c.io.dataOut)
    poke(c.io.readRow, ii/3)
    step(1)
    println("////////////////////")
  }
}
