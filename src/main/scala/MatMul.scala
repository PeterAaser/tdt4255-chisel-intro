package Ex0

import chisel3._
import chisel3.util.Counter
import chisel3.experimental.MultiIOModule

class MatMul(val rowDimsA: Int, val colDimsA: Int) extends MultiIOModule {

  val io = IO(
    new Bundle {
      val dataInA     = Input(UInt(32.W))
      val dataInB     = Input(UInt(32.W))

      val dataOut     = Output(UInt(32.W))
      val outputValid = Output(Bool())
    }
  )

  val debug = IO(
    new Bundle {
      val myDebugSignal = Output(Bool())
    }
  )


  /**
    * Your code here
    */
  val matrixA     = Module(new Matrix(rowDimsA, colDimsA)).io
  val matrixB     = Module(new Matrix(rowDimsA, colDimsA)).io
  val dotProdCalc = Module(new DotProd(colDimsA)).io

  matrixA.dataIn      := 0.U
  matrixA.rowIdx      := 0.U
  matrixA.colIdx      := 0.U
  matrixA.writeEnable := false.B

  matrixB.rowIdx      := 0.U
  matrixB.colIdx      := 0.U
  matrixB.dataIn      := 0.U
  matrixB.writeEnable := false.B

  dotProdCalc.dataInA := 0.U
  dotProdCalc.dataInB := 0.U

  io.dataOut := 0.U
  io.outputValid := false.B


  debug.myDebugSignal := false.B
}
