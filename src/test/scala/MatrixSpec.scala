package Ex0

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import TestUtils._

import scala.collection.immutable.{ Vector => _ }

class MatrixSpec extends FlatSpec with Matchers {
  import MatrixTests._

  behavior of "Matrix"

  val rand = new scala.util.Random(100)
  val rowDims = 5
  val colDims = 3

  it should "Update its contents with a square shape" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new Matrix(rowDims,rowDims)) { c =>
        new UpdatesData(c)
      } should be(true)
    )
  }


  it should "Update its contents with a rectangular shape" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new Matrix(rowDims,colDims)) { c =>
        new UpdatesData(c)
      } should be(true)
    )
  }


  it should "Retain its contents when writeEnable is low" in {
    wrapTester(
      chisel3.iotesters.Driver(() => new Matrix(rowDims, colDims)) { c =>
        new RetainsData(c)
      } should be(true)
    )
  }
}

object MatrixTests {

  val rand = new scala.util.Random(100)

  class UpdatesData(c: Matrix) extends PeekPokeTester(c) {

    val inputs = List.fill(c.colsDim){
      List.fill(c.rowsDim)(rand.nextInt(20) + 1)
    }

    poke(c.io.writeEnable, true)
    for(col <- 0 until c.colsDim){
      for(row <- 0 until c.rowsDim){
        poke(c.io.colIdx, col)
        poke(c.io.rowIdx, row)
        poke(c.io.dataIn, inputs(col)(row))
        step(1)
      }
    }

    for(col <- 0 until c.colsDim){
      for(row <- 0 until c.rowsDim){
        poke(c.io.colIdx, col)
        poke(c.io.rowIdx, row)
        expect(c.io.dataOut, inputs(col)(row))
        step(1)
      }
    }
  }


  class RetainsData(c: Matrix) extends PeekPokeTester(c) {

    val inputs = List.fill(c.colsDim){
      List.fill(c.rowsDim)(rand.nextInt(20) + 1)
    }

    poke(c.io.writeEnable, true)
    for(col <- 0 until c.colsDim){
      for(row <- 0 until c.rowsDim){
        poke(c.io.colIdx, col)
        poke(c.io.rowIdx, row)
        poke(c.io.dataIn, inputs(col)(row))
        step(1)
      }
    }

    poke(c.io.writeEnable, false)

    for(col <- 0 until c.colsDim){
      for(row <- 0 until c.rowsDim){
        poke(c.io.colIdx, col)
        poke(c.io.rowIdx, row)
        poke(c.io.dataIn, 0)
        step(1)
      }
    }

    for(col <- 0 until c.colsDim){
      for(row <- 0 until c.rowsDim){
        poke(c.io.colIdx, col)
        poke(c.io.rowIdx, row)
        expect(c.io.dataOut, inputs(col)(row))
        step(1)
      }
    }
  }
}
