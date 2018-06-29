package Core
import chisel3._
import chisel3.core.Input
import chisel3.iotesters.PeekPokeTester
import utilz._

/**
  DaisyGrids hold n daisyVecs. Unlike the daisyVecs, daisyGrids have a select signal for selecting
  which daisyVec to work on, but these daisyVecs can not be controlled from the outside.
  */
class daisyGrid(dims: Dims, dataWidth: Int) extends Module{

  val io = IO(new Bundle {

    val writeEnable = Input(Bool())
    val dataIn     = Input(UInt(dataWidth.W))
    val rowSelect    = Input(UInt(8.W))

    val dataOut    = Output(UInt(dataWidth.W))
  })

  val rows = Array.fill(dims.rows){ Module(new daisyVector(dims.cols, dataWidth)).io }

  /**
    Your implementation here
    */


  /**
    LF
    */
  io.dataOut := 0.U

  for(ii <- 0 until dims.rows){

    rows(ii).writeEnable := 0.U
    rows(ii).dataIn := io.dataIn

    when(io.rowSelect === ii.U ){
      rows(ii).writeEnable := io.writeEnable
      io.dataOut := rows(ii).dataOut
    }
  }
}
