package Ov1

import chisel3._
import chisel3.core.Input
import chisel3.iotesters.PeekPokeTester

/**
  Decoder should read the top 6 bits and output

  Branch
  MemRead
  MemtoReg
  ALUOp
  memWrite
  ALUSrc
  RegWrite
  */

class ControlSignals extends Bundle(){
  val Branch   = Output(Bool())
  val MemRead  = Output(Bool())
  val MemtoReg = Output(Bool())
  val MemWrite = Output(Bool())
  val ALUSrc   = Output(Bool())
  val RegWrite = Output(Bool())

}

// class myDecoder(val hurr: Int) extends Module {

// }
