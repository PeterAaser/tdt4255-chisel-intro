package Ex0

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}

object TestUtils {

  def genMatrix(rows: Int, cols: Int) = List.fill(rows)(
    List.fill(cols)(scala.util.Random.nextInt(5))
  )

  def printVector(v: List[Int]): String =
    v.map(x => "%3d".format(x)).mkString("[",",","]")

  def printMatrix(m: List[List[Int]]): String =
    "\n" + m.map(printVector).mkString("\n")

  def dotProduct(xs: List[Int], ys: List[Int]): Int =
    (for ((x, y) <- xs zip ys) yield x * y) sum

  def matrixMultiply(ma: List[List[Int]], mb: List[List[Int]]): List[List[Int]] =
    ma.map(mav => mb.transpose.map(mbv => dotProduct(mav,mbv)))


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
