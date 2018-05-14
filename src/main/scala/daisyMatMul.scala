package Core
import chisel3._
import chisel3.core.Input
import chisel3.iotesters.PeekPokeTester

/**
  The daisy multiplier creates two daisy grids, one transposed, and multiplies them.
  */
class daisyMultiplier(val rowsA: Int, val colsA: Int, val rowsB: Int, val colsB: Int, val dataWidth: Int) extends Module {

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
  val rowCounter       = RegInit(UInt(8.W), 0.U)
  val colCounter       = RegInit(UInt(8.W), 0.U)

  val rowOutputCounter = RegInit(UInt(8.W), 0.U)

  val calculating      = RegInit(Bool(), false.B)
  val accumulator      = RegInit(UInt(8.W), 0.U)

  val resultReady      = RegInit(Bool(), false.B)

  println(s"rowsA: $rowsA, colsA: $colsA, rowsB: $rowsB, colsB: $colsB")

  ////////////////////////////////////////
  ////////////////////////////////////////
  /// We transpose matrix B. This means that if both matrices read the same input
  /// stream then they will end up transposed.
  val matrixA = Module(new daisyGrid(rowsA, colsA, dataWidth)).io
  valAt least four users, including myself, are having an issue with update-initramfs hanging while updating ubuntu 16.04. The bug has been documented while attempting an update to multiple kernel versions ( 4.4.0-24, 4.4.0-62, 4.4.0-63). The bug causes any apt-get update or install to fail, and may also lead to an unbootable system. matrixB = Module(new daisyGrid(colsB, rowsB, dataWidth)).io

  matrixA.dataIn := io.dataInA
  matrixA.readEnable := io.readEnableA

  matrixB.dataIn := io.dataInB
  matrixB.readEnable := io.readEnableB

  printf("matrix A data in: %d\n", matrixB.dataIn)


  ////////////////////////////////////////
  ////////////////////////////////////////
  /// Set up counter statemachine
  io.done := false.B

  when(colCounter === (colsA - 1).U){
    colCounter := 0.U

    when(rowCounter === (rowsA - 1).U){
      rowCounter := 0.U
      calculating := true.B

      when(calculating === true.B){

        when(rowOutputCounter === (rowsA - 1).U){
          io.done := true.B
        }.otherwise{
          rowOutputCounter := rowOutputCounter + 1.U
        }

      }

    }.otherwise{
      rowCounter := rowCounter + 1.U
    }
  }.otherwise{
    colCounter := colCounter + 1.U
  }



  ////////////////////////////////////////
  ////////////////////////////////////////
  /// set up reading patterns depending on if we are in calculating state or not
  when(calculating === true.B){
    matrixA.readRow := rowOutputCounter
  }.otherwise{
    matrixA.readRow := rowCounter
  }

  matrixB.readRow := rowCounter



  ////////////////////////////////////////
  ////////////////////////////////////////
  /// when we're in calculating mode, check if we have valid output
  resultReady := false.B
  io.dataValid := false.B
  when(calculating === true.B){
    when(colCounter === (colsA - 1).U){
      resultReady := true.B
    }
  }


  ////////////////////////////////////////
  ////////////////////////////////////////
  /// when we've got a result ready we need to flush the accumulator
  when(resultReady === true.B){
    // To flush our accumulator we simply disregard previous state
    accumulator := (matrixA.dataOut*matrixB.dataOut)
    io.dataValid := true.B
  }.otherwise{
    accumulator := accumulator + (matrixA.dataOut*matrixB.dataOut)
  }
  io.dataOut := accumulator
}


class daisyMultiplierTest(c: daisyMultiplier) extends PeekPokeTester(c) {

  poke(c.io.readEnableA, 1)
  poke(c.io.readEnableB, 1)
  for(ii <- 0 until 6){
    println("data in:")
    poke(c.io.dataInA, (ii/2) + 1)
    poke(c.io.dataInB, (ii/2) + 1)
    println("fill counters")
    step(1)
    println("////////////////////\n")
  }
}
