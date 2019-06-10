package Ex0

import chisel3._
import chisel3.experimental._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._

import scala.collection.immutable.{ Vector => _ }

class VectorSpec extends FlatSpec with Matchers {
  import VectorTests._

  val elements = 7

  behavior of "Vector"

  it should "Not update its contents when write enable is false" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new Vector(elements)) { c =>
        new WriteEnable(c)
      } should be(true)
    )
  }

  it should "Update its registers when write enable is true" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new Vector(elements)) { c =>
        new UpdatesData(c)
      } should be(true)
    )
  }

  it should "Retain its data once write enable is set to false" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new Vector(elements)) { c =>
        new RetainsData(c)
      } should be(true)
    )
  }
}


object VectorTests {
  
  class WriteEnable(c: Vector) extends PeekPokeTester(c)  {

    poke(c.io.dataIn, 123)
    poke(c.io.writeEnable, false)

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

    poke(c.io.writeEnable, true)

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

    poke(c.io.writeEnable, true)

    for(ii <- 0 until c.elements){
      poke(c.io.idx, ii)
      poke(c.io.dataIn, ii)
      step(1)
    }

    poke(c.io.writeEnable, false)

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
