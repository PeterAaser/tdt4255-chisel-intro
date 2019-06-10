package Ex0
import chisel3._
import java.io.File

object main {
  def main(args: Array[String]): Unit = {
    val s = """
    | Attempting to "run" a chisel program is rather meaningless.
    | Instead, try running the tests, for instance with "test" or "testOnly Examples.MyIncrementTest
    | 
    | If you want to create chisel graphs, simply remove this message and comment in the code underneath 
    | to generate the modules you're interested in.
    """.stripMargin
    println(s)
  }

  // Uncomment to dump .fir file
  // val f = new File("MatMul.fir")
  // chisel3.Driver.dumpFirrtl(chisel3.Driver.elaborate(() => new MatMul(5, 4)), Option(f))

}

