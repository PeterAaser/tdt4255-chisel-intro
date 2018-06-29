
package Core
import Core.daisyVector
import chisel3._
import chisel3.core.Input
import chisel3.iotesters.PeekPokeTester
import chisel3.util.Counter
import utilz._

/**
  The daisy multiplier creates two daisy grids, one transposed, and multiplies them.
  */
class daisyVecMat(matrixDims: Dims, dataWidth: Int) extends Module {

  val io = IO(
    new Bundle {

      val dataInA     = Input(UInt(dataWidth.W))
      val writeEnableA = Input(Bool())

      val dataInB     = Input(UInt(dataWidth.W))
      val writeEnableB = Input(Bool())

      val dataOut     = Output(UInt(dataWidth.W))
      val dataValid   = Output(Bool())
      val done        = Output(Bool())

    }
  )

  /**
    The dimensions are transposed because this is a vector * matrix multiplication

                [1, 2]
    [a, b, c] x [3, 4]
                [5, 6]

    Here the vector will output a, b, c, a, b, c, a...
    The Matrix is the type you made last exercise, so it is actually just 3 more vectors
    of length 2. In cycle 0 the values {1, 3, 5} may be selected, in cycle 1 {2, 4, 6}
    can be selected.

    However, you can make up for the impedance mismatch by transposing the matrix, storing
    the data in 2 vectors of length 3 instead.

    In memory matrixB will look like [1, 3, 5]
                                     [2, 4, 6]

    For a correct result, it is up to the user to input the data for matrixB in a transposed
    manner. This is done in the tests, you don't need to worry about it.
  */
  val dims = matrixDims.transposed

  // basic linAlg
  val lengthA = dims.cols

  val vecA                 = Module(new daisyVector(lengthA, dataWidth)).io
  val matrixB              = Module(new daisyGrid(dims, dataWidth)).io
  val dotProductCalculator = Module(new daisyDot(lengthA, dataWidth)).io
  val dataIsLoaded         = RegInit(Bool(), false.B)

  /**
    Your implementation here
    */
  // Create counters to keep track of when the matrix and vector has gotten all the data.
  // You can assume that writeEnable will be synchronized with the vectors. I.e for a vector
  // of length 3 writeEnable can only go from true to false and vice versa at T = 0, 3, 6, 9 etc


  // Create counters to keep track of how far along the computation is.

  // Set up the correct rowSelect for matrixB

  // Wire up write enables for matrixB and vecA

  /**
    In the solution I used the following to keep track of state
    You can use these if you want to, or do it however you see fit.
    */
  // val currentCol = Counter(dims.cols)
  // val rowSel = Counter(dims.rows)
  // val aReady = RegInit(Bool(), false.B)
  // val bReady = RegInit(Bool(), false.B)
  // val isDone = RegInit(Bool(), false.B)
  // val (inputCounterB, counterBWrapped) = Counter(io.writeEnableB, (dims.elements) - 1)
  // val (numOutputted, numOutputtedWrapped) = Counter(dataValid, lengthA)
  // val (inputCounterA, counterAWrapped) = Counter(io.writeEnableA, lengthA - 1)

  /**
    LF
    */
  val dataValid = Wire(Bool())


  ////////////////////////////////////////
  ////////////////////////////////////////
  /// Wire components
  vecA.dataIn := io.dataInA
  vecA.writeEnable := io.writeEnableA

  matrixB.dataIn := io.dataInB
  matrixB.writeEnable := io.writeEnableB

  io.dataOut := dotProductCalculator.dataOut

  // allows us to use dataValid internally
  io.dataValid := dataValid

  dotProductCalculator.dataInA := vecA.dataOut
  dotProductCalculator.dataInB := matrixB.dataOut
  dataValid := dotProductCalculator.outputValid & dataIsLoaded

  ////////////////////////////////////////
  ////////////////////////////////////////
  /// Select the correct row
  val currentCol = Counter(dims.cols)
  val rowSel = Counter(dims.rows)

  when(currentCol.inc()){
    rowSel.inc()
  }

  matrixB.rowSelect := rowSel.value


  ////////////////////////////////////////
  ////////////////////////////////////////
  /// Check if data is loaded
  val aReady = RegInit(Bool(), false.B)
  val bReady = RegInit(Bool(), false.B)

  val (inputCounterA, counterAWrapped) = Counter(io.writeEnableA, lengthA - 1)
  when(counterAWrapped){ aReady := true.B }

  val (inputCounterB, counterBWrapped) = Counter(io.writeEnableB, (dims.elements) - 1)
  when(counterBWrapped){ bReady := true.B }

  dataIsLoaded := aReady & bReady


  ////////////////////////////////////////
  ////////////////////////////////////////
  /// Check if we're done
  val isDone = RegInit(Bool(), false.B)
  val (numOutputted, numOutputtedWrapped) = Counter(dataValid, lengthA)

  when(numOutputtedWrapped){ isDone := true.B }

  io.done := isDone
}
