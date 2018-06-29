package Core
import chisel3._
import chisel3.iotesters._
import org.scalatest.{Matchers, FlatSpec}
import testUtils._
import utilz._


class daisyGridSpec extends FlatSpec with Matchers {

  behavior of "daisy grid"

  def writeRowCheck(dims: Dims, rowSel: Int => Int): Seq[CycleTask[daisyGrid]] = {
    (0 until dims.cols).map( n =>
      CycleTask[daisyGrid](
        n,
        d => d.poke(d.dut.io.dataIn, n),
        d => d.poke(d.dut.io.writeEnable, 1),
        d => d.poke(d.dut.io.rowSelect, rowSel(n)))
    ) ++
      (0 until dims.cols*2).map( n =>
        CycleTask[daisyGrid](
          n,
          d => d.poke(d.dut.io.dataIn, 0),
          d => d.poke(d.dut.io.writeEnable, 0),
          d => d.poke(d.dut.io.rowSelect, rowSel(n)),
          d => d.expect(d.dut.io.dataOut, n % dims.cols)).delay(dims.cols)
      )
  }


  val dims = Dims(rows = 4, cols = 5)

  it should "work like a regular daisyVec when row select is fixed to 0" in {

    iotesters.Driver.execute(() => new daisyGrid(dims, 32), new TesterOptionsManager) { c =>
      IoSpec[daisyGrid](writeRowCheck(dims, _ => 0), c).myTester
    } should be(true)
  }


  it should "work like a regular daisyVec when row select is fixed to 1" in {
    iotesters.Driver.execute(() => new daisyGrid(dims, 32), new TesterOptionsManager) { c =>
      IoSpec[daisyGrid](writeRowCheck(dims, _ => 1), c).myTester
    } should be(true)
  }


  it should "be able to write a matrix and output it" in {
    iotesters.Driver.execute(() => new daisyGrid(dims, 32), new TesterOptionsManager) { c =>

      def writeMatrix(matrix: Matrix): List[CycleTask[daisyGrid]] = {
        (0 until dims.elements).toList.zipWithIndex.map{ case(n, idx) =>
          val row = n / dims.cols
          CycleTask[daisyGrid](
            n,
            d => d.poke(d.dut.io.dataIn, n),
            d => d.poke(d.dut.io.writeEnable, 1),
            d => d.poke(d.dut.io.rowSelect, row))
        }
      }

      def readMatrix(matrix: Matrix): List[CycleTask[daisyGrid]] = {
        (0 until dims.elements).toList.zipWithIndex.map{ case(n, idx) =>
          val row = n / dims.cols
          CycleTask[daisyGrid](
            n,
            d => d.poke(d.dut.io.dataIn, 0),
            d => d.poke(d.dut.io.writeEnable, 0),
            d => d.poke(d.dut.io.rowSelect, row),
            d => d.expect(d.dut.io.dataOut, n))
        }
      }


      val m = genMatrix(Dims(rows = 4, cols = 5))
      val ins = writeMatrix(m) ++ readMatrix(m).map(_.delay(dims.elements))

      IoSpec[daisyGrid](ins, c).myTester
    } should be(true)
  }
}
