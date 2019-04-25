package Ex0

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}

object TestUtils {

  def wrapTester(test: => Unit): Unit = {
    try { test }
    catch { 
      case e: firrtl.passes.CheckInitialization.RefNotInitializedException => {
        println("##########################################################")
        println("##########################################################")
        println("##########################################################")
        println("Your design has unconnected wires!")
        println("error:\n")
        println(e.getMessage)
        println("")
        println("")
        println("##########################################################")
        println("##########################################################")
        println("##########################################################")
      }
      case e: Exception => throw e
    }
  }
}
