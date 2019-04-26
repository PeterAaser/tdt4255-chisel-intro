package Ex0

import chisel3._

// This import statement makes the scala vector invisible, reducing confusion
import scala.collection.immutable.{ Vector => _ }

class Matrix(val rowsDim: Int, val colsDim: Int) extends Module {

  val io = IO(
    new Bundle {
      val colIdx     = Input(UInt(32.W))
      val rowIdx     = Input(UInt(32.W))
      val dataIn     = Input(UInt(32.W))
      val readEnable = Input(Bool())

      val dataOut    = Output(UInt(32.W))
    }
  )

  /**
    * Your code here
    */

  // Creates a vector of zero-initialized registers
  val rows = Vec.fill(rowsDim)(Module(new Vector(colsDim)).io)

  // placeholders
  io.dataOut := 0.U
  for(ii <- 0 until rowsDim){
    rows(ii).dataIn     := 0.U
    rows(ii).readEnable := false.B
    rows(ii).idx        := 0.U
  }


  /**
    * LF
    */
  for(ii <- 0 until rowsDim){

    rows(ii).dataIn := io.dataIn
    rows(ii).idx    := io.colIdx

    when(ii.U === io.rowIdx){
      rows(ii).readEnable := io.readEnable
    }.otherwise{
      rows(ii).readEnable := false.B
    }
  }
  io.dataOut := rows(io.rowIdx).dataOut

}
