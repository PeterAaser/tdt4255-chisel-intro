package Ex0

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._

class DotProdSpec extends FlatSpec with Matchers {
  import DotProdTests._

  val rand = new scala.util.Random(100)
  val elements = 7

  behavior of "DotProd"

  it should "Only signal valid output at end of calculation" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new DotProd(elements)) { c =>
        new SignalsWhenDone(c)
      } should be(true)
    )
  }


  it should "Calculate the correct output" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new DotProd(elements)) { c =>
        new CalculatesCorrectResult(c)
      } should be(true)
    )
  }


  it should "Calculate the correct output and signal when appropriate" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new DotProd(elements)) { c =>
        new CalculatesCorrectResultAndSignals(c)
      } should be(true)
    )
  }
}

object DotProdTests {

  val rand = new scala.util.Random(100)

  class SignalsWhenDone(c: DotProd) extends PeekPokeTester(c) {

    for(ii <- 0 until c.elements - 1){
      expect(c.io.outputValid, false)
      step(1)
    }
    expect(c.io.outputValid, true)
    step(1)

    for(ii <- 0 until c.elements - 1){
      expect(c.io.outputValid, false)
      step(1)
    }
    expect(c.io.outputValid, true)
    step(1)
  }


  class CalculatesCorrectResult(c: DotProd) extends PeekPokeTester(c) {

    val inputsA = List.fill(c.elements)(rand.nextInt(10))
    val inputsB = List.fill(c.elements)(rand.nextInt(10))
    println("runnign dot prod calc with inputs:")
    println(inputsA.mkString("[", "] [", "]"))
    println(inputsB.mkString("[", "] [", "]"))
    val expectedOutput = (for ((a, b) <- inputsA zip inputsB) yield a * b) sum

    for(ii <- 0 until c.elements){
      poke(c.io.dataInA, inputsA(ii))
      poke(c.io.dataInB, inputsB(ii))
      if(ii == c.elements - 1)
        expect(c.io.dataOut, expectedOutput)
      step(1)
    }
  }


  class CalculatesCorrectResultAndSignals(c: DotProd) extends PeekPokeTester(c) {

    val inputsA = List.fill(c.elements)(rand.nextInt(10))
    val inputsB = List.fill(c.elements)(rand.nextInt(10))
    println("runnign dot prod calc with inputs:")
    println(inputsA.mkString("[", "] [", "]"))
    println(inputsB.mkString("[", "] [", "]"))
    val expectedOutput = (for ((a, b) <- inputsA zip inputsB) yield a * b) sum

    for(ii <- 0 until c.elements){
      poke(c.io.dataInA, inputsA(ii))
      poke(c.io.dataInB, inputsB(ii))
      if(ii == c.elements - 1){
        expect(c.io.dataOut, expectedOutput)
        expect(c.io.outputValid, true)
      }
      else
        expect(c.io.outputValid, false)
      step(1)
    }


    for(ii <- 0 until c.elements){
      poke(c.io.dataInA, inputsA(ii))
      poke(c.io.dataInB, inputsB(ii))
      if(ii == c.elements - 1){
        expect(c.io.dataOut, expectedOutput)
        expect(c.io.outputValid, true)
      }
      else
        expect(c.io.outputValid, false)
      step(1)
    }
  }
}
