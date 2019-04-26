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
      val ready        = Output(Bool())
      val dpValid      = Output(Bool())

      val rowSelA      = Output(UInt(32.W))
      val rowSelB      = Output(UInt(32.W))
      val colSel       = Output(UInt(32.W))

      val ma2dp        = Output(UInt(32.W))
      val mb2dp        = Output(UInt(32.W))
    }
  )


  /**
    * Your code here
    */
  val matrixA     = Module(new Matrix(rowDimsA, colDimsA)).io
  val matrixB     = Module(new Matrix(rowDimsA, colDimsA)).io
  val dotProdCalc = Module(new DotProd(colDimsA)).io

  // matrixA.dataIn      := 0.U
  // matrixA.rowIdx      := 0.U
  // matrixA.colIdx      := 0.U
  // matrixA.readEnable  := false.B

  // matrixB.rowIdx      := 0.U
  // matrixB.colIdx      := 0.U
  // matrixB.dataIn      := 0.U
  // matrixB.readEnable  := false.B

  // dotProdCalc.dataInA := 0.U
  // dotProdCalc.dataInB := 0.U

  // io.dataOut := 0.U
  // io.outputValid := false.B


  /**
    * LF
    */


  // Get the data in
  val ready = RegInit(false.B)

  val (colCounter, colCounterWrap) = Counter(true.B, colDimsA)
  val (rowSelA, rowSelAWrap)       = Counter(colCounterWrap, rowDimsA)
  val (rowSelB, _)                 = Counter(rowSelAWrap & ready, rowDimsA * colDimsA)

  when(!ready){
    ready := rowSelAWrap
    matrixA.readEnable := true.B
    matrixB.readEnable := true.B

    matrixA.colIdx := colCounter
    matrixA.rowIdx := rowSelA

    matrixB.colIdx := colCounter
    matrixB.rowIdx := rowSelA

  }.otherwise{
    matrixA.readEnable := false.B
    matrixB.readEnable := false.B

    matrixA.colIdx := colCounter
    matrixA.rowIdx := rowSelB

    matrixB.colIdx := colCounter
    matrixB.rowIdx := rowSelA
  }


  matrixA.dataIn := io.dataInA
  matrixB.dataIn := io.dataInB

  dotProdCalc.dataInA := matrixA.dataOut
  dotProdCalc.dataInB := matrixB.dataOut

  io.dataOut     := dotProdCalc.dataOut
  io.outputValid := dotProdCalc.outputValid & ready

  debug.ready        := ready
  debug.dpValid      := dotProdCalc.outputValid
  debug.rowSelA      := rowSelA
  debug.rowSelB      := rowSelB
  debug.colSel       := colCounter

  debug.ma2dp        := matrixA.dataOut
  debug.mb2dp        := matrixB.dataOut

}
