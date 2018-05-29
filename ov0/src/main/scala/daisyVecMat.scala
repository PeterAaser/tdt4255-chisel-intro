
package Core
import Core.daisyVector
import chisel3._
import chisel3.core.Input
import chisel3.iotesters.PeekPokeTester
import chisel3.util.Counter

/**
  The daisy multiplier creates two daisy grids, one transposed, and multiplies them.
  */
class daisyVecMat(val lengthA: Int, val rowsB: Int, val colsB: Int, val dataWidth: Int) extends Module {

  val io = IO(new Bundle {

    val dataInA     = Input(UInt(dataWidth.W))
    val readEnableA = Input(Bool())

    val dataInB     = Input(UInt(dataWidth.W))
    val readEnableB = Input(Bool())

    val dataOut     = Output(UInt(dataWidth.W))
    val dataValid   = Output(Bool())
    val done        = Output(Bool())
  })

  // How many cycles does it take to fill the matrices with data?


  ////////////////////////////////////////
  ////////////////////////////////////////
  /// We transpose matrix B.
  val vecA                 = Module(new daisyVector(lengthA, dataWidth)).io
  val matrixB              = Module(new daisyGrid(colsB, rowsB, dataWidth)).io
  val dotProductCalculator = Module(new daisyDot(lengthA, dataWidth)).io
  val dataIsLoaded         = RegInit(Bool(), false.B)

  /**
    Your implementation here
    */

  /**
    LF
    */
  val dataValid = Wire(Bool())


  ////////////////////////////////////////
  ////////////////////////////////////////
  /// Wire components
  vecA.dataIn := io.dataInA
  vecA.readEnable := io.readEnableA

  matrixB.dataIn := io.dataInB
  matrixB.readEnable := io.readEnableB

  io.dataOut := dotProductCalculator.dataOut

  // allows us to use dataValid internally
  io.dataValid := dataValid

  dotProductCalculator.dataInA := vecA.dataOut
  dotProductCalculator.dataInB := matrixB.dataOut
  dataValid := dotProductCalculator.outputValid & dataIsLoaded

  ////////////////////////////////////////
  ////////////////////////////////////////
  /// Select the correct row
  val (currentCol, colDone) = Counter(true.B, colsB)
  val (rowSel, _) = Counter(colDone, rowsB)
  matrixB.rowSelect := rowSel


  ////////////////////////////////////////
  ////////////////////////////////////////
  /// Check if data is loaded
  val aReady = RegInit(Bool(), false.B)
  val bReady = RegInit(Bool(), false.B)

  val (inputCounterA, counterAWrapped) = Counter(io.readEnableA, lengthA - 1)
  when(counterAWrapped){ aReady := true.B }

  val (inputCounterB, counterBWrapped) = Counter(io.readEnableB, colsB*rowsB)
  when(counterBWrapped){ bReady := true.B }

  dataIsLoaded := aReady & bReady


  ////////////////////////////////////////
  ////////////////////////////////////////
  /// Check if we're done
  val isDone = RegInit(Bool(), false.B)
  val (numOutputted, numOutputtedWrapped) = Counter(dataValid, lengthA)

  when(numOutputtedWrapped){ isDone := true.B }


  // printf(p"dataInA     = ${io.dataInA}\n")
  // printf(p"validA      = ${io.readEnableA}\n")
  // printf(p"dataInB     = ${io.dataInB}\n")
  // printf(p"validB      = ${io.readEnableB}\n")
  // printf(p"validOut    = ${io.dataValid}\n")
  // printf(p"data loaded = ${dataIsLoaded}\n")
  // printf(p"aReady      = ${aReady}\n")
  // printf(p"bReady      = ${bReady}\n")

  // printf(p"counter A      = ${inputCounterA}\n")
  // printf(p"counter B      = ${inputCounterB}\n")

  // printf(p"out         = ${dotProductCalculator.dataOut}\n\n")




  io.done := isDone

}
