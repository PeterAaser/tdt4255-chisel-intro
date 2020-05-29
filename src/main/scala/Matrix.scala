package Ex0

import chisel3._

// This import statement makes the scala vector invisible, reducing confusion
import scala.collection.immutable.{ Vector => _ }

class Matrix(val rowsDim: Int, val colsDim: Int) extends Module {

  val io = IO(
    new Bundle {
      val colIdx      = Input(UInt(32.W))
      val rowIdx      = Input(UInt(32.W))
      val dataIn      = Input(UInt(32.W))
      val writeEnable = Input(Bool())

      val dataOut     = Output(UInt(32.W))
    }
  )

  /**
    * Placeholders.
    *
    * You can delete these when you see fit, they're just here so
    * that the circuit is valid and compiles/synthesizes correctly.
    *
    * In your finished work these can be deleted.
    */
  io.dataOut := 0.U
  for(ii <- 0 until rowsDim){
    rows(ii).dataIn      := 0.U
    rows(ii).writeEnable := false.B
    rows(ii).idx         := 0.U
  }


  /**
    * Your code here
    */
  // Fill a Vec with your Vector from the previous exercise.
  // The naming conflict is a little unfortunate.
  val rows = Vec.fill(rowsDim)(Module(new Vector(colsDim)).io)

  // When writeEnable is high, use rowIdx to select which row you want to operate on.
  when(io.writeEnable){
    val huh = rows(io.rowIdx)
  }

}
