
package Core
import chisel3._
import chisel3.core.Input
import chisel3.iotesters.PeekPokeTester


class myIncrement(incrementBy: Int) extends Module {
  val io = IO(
    new Bundle {
      val dataIn  = Input(UInt(32.W))
      val dataOut = Output(UInt(32.W))
    }
  )

  io.dataOut := io.dataIn + incrementBy.U
}


class myIncrementTwice(incrementBy: Int) extends Module {
  val io = IO(
    new Bundle {
      val dataIn  = Input(UInt(32.W))
      val dataOut = Output(UInt(32.W))
    }
  )

  val first  = Module(new myIncrement(incrementBy))
  val second = Module(new myIncrement(incrementBy))

  first.io.dataIn  := io.dataIn
  second.io.dataIn := first.io.dataOut

  io.dataOut := second.io.dataOut
}


class myIncrementN(incrementBy: Int, numIncrementors: Int) extends Module {
  val io = IO(
    new Bundle {
      val dataIn  = Input(UInt(32.W))
      val dataOut = Output(UInt(32.W))
    }
  )

  val incrementors = Array.fill(numIncrementors){ Module(new myIncrement(incrementBy)) }

  for(ii <- 1 until numIncrementors){
    incrementors(ii).io.dataIn := incrementors(ii - 1).io.dataOut
  }

  incrementors(0).io.dataIn := io.dataIn
  io.dataOut := incrementors(numIncrementors).io.dataOut
}




class myDelay() extends Module {
  val io = IO(
    new Bundle {
      val dataIn  = Input(UInt(32.W))
      val dataOut = Output(UInt(32.W))
    }
  )

  val reg = RegInit(UInt(32.W), 0.U)
  reg := io.dataIn
  io.dataOut := reg
}


class myDelayN(steps: Int) extends Module {
  val io = IO(
    new Bundle {
      val dataIn  = Input(UInt(32.W))
      val dataOut = Output(UInt(32.W))
    }
  )

  val delayers = Array.fill(steps){ Module(new myDelay()) }

  for(ii <- 1 until steps){
    delayers(ii).io.dataIn := delayers(ii - 1).io.dataOut
  }

  delayers(0).io.dataIn := io.dataIn
  io.dataOut := delayers(steps).io.dataOut
}


class mySelector(numValues: Int) extends Module {
  val io = IO(
    new Bundle {
      val next      = Input(Bool())
      val dataOut   = Output(UInt(32.W))
      val newOutput = Output(Bool())
    }
  )

  val counter = RegInit(UInt(Chisel.log2Up(numValues).W), 0.U)
  val nextOutputIsFresh = RegInit(Bool(), true.B)

  /**
    Generate random values. Using the when keyword we choose which random
    value should drive the dataOut signal
   */
  io.dataOut := 0.U
  List.fill(numValues)(scala.util.Random.nextInt(100)).zipWithIndex.foreach {
    case(rand, idx) =>
      when(counter === idx.U){
        if(rand < 50)
          io.dataOut := rand.U
        else
          io.dataOut := (rand + 100).U
      }
  }

  /**
    While chisel comes with an inbuilt Counter, we implement ours the old fashion way
    There are far more elegant ways of implementing this, read the chisel docs, discuss
    best practice among yourselves and experiment!
    */
  nextOutputIsFresh := true.B
  when(io.next === true.B){
    when(counter < (numValues - 1).U){
      counter := counter + 1.U
    }.otherwise {
      counter := 0.U
    }
  }.otherwise {
    nextOutputIsFresh := false.B
  }
  io.newOutput := nextOutputIsFresh
}


class mySelectorTest(c: mySelector) extends PeekPokeTester(c) {
  poke(c.io.next, true.B)
  for(ii <- 0 until 10){
    val wasStale = peek(c.io.newOutput) == 0
    val output = peek(c.io.dataOut).toString()
    println(s"at step $ii:")
    println(s"data out is $output")
    println(s"was the output fresh? ${!wasStale}")
    println()
    step(1)
  }

  poke(c.io.next, false.B)

  for(ii <- 0 until 3){
    val wasStale = peek(c.io.newOutput) == 0
    val output = peek(c.io.dataOut).toString()
    println(s"at step $ii:")
    println(s"data out is $output")
    println(s"was the output fresh? ${!wasStale}")
    println()
    step(1)
  }
}
