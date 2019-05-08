package Ex0

import chisel3._
import chisel3.experimental._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._

import scala.collection.immutable.{ Vector => _ }

class VectorSpec extends FlatSpec with Matchers {
  import VectorTests._

  val elements = scala.util.Random.nextInt(5) + 2

  behavior of "Vector"

  it should "Not read data when read enable is false" in {
    // FileUtils.getSvg("Adder")
    wrapTester(
      chisel3.iotesters.Driver(() => new Vector(elements)) { c =>
        new ReadEnable(c)
      } should be(true)
    )
  }

  // it should "Update its registers when read enable is true" in {
  //   wrapTester(
  //     chisel3.iotesters.Driver(() => new Vector(elements)) { c =>
  //       new UpdatesData(c)
  //     } should be(true)
  //   )
  // }

  // it should "Retain its data once read enable is set to false" in {
  //   wrapTester(
  //     chisel3.iotesters.Driver(() => new Vector(elements)) { c =>
  //       new RetainsData(c)
  //     } should be(true)
  //   )
  // }
}


object VectorTests {
  
  class ReadEnable(c: Vector) extends PeekPokeTester(c)  {

    poke(c.io.dataIn, 123)
    poke(c.io.readEnable, false)

    for(ii <- 0 until c.elements){
      poke(c.io.idx, ii)
      step(1)
      expect(c.io.dataOut, 0)
    }

    poke(c.io.dataIn, 124)
    for(ii <- 0 until c.elements){
      poke(c.io.idx, ii)
      step(1)
      expect(c.io.dataOut, 0)
    }
  }


  class UpdatesData(c: Vector) extends PeekPokeTester(c)  {

    poke(c.io.readEnable, true)

    for(ii <- 0 until c.elements){
      poke(c.io.idx, ii)
      poke(c.io.dataIn, ii)
      step(1)
    }

    for(ii <- 0 until c.elements){
      poke(c.io.idx, ii)
      expect(c.io.dataOut, ii)
      step(1)
    }
  }


  class RetainsData(c: Vector) extends PeekPokeTester(c)  {

    poke(c.io.readEnable, true)

    for(ii <- 0 until c.elements){
      poke(c.io.idx, ii)
      poke(c.io.dataIn, ii)
      step(1)
    }

    poke(c.io.readEnable, false)

    for(ii <- 0 until c.elements){
      poke(c.io.idx, ii)
      expect(c.io.dataOut, ii)
      step(1)
    }


    for(ii <- 0 until c.elements){
      poke(c.io.idx, ii)
      expect(c.io.dataOut, ii)
      step(1)
    }
  }

}
