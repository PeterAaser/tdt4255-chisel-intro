package Ex0
import chisel3._
import java.io.File

object main {
  def main(args: Array[String]): Unit = {
    val s = """
    | Attempting to "run" a chisel program is rather meaningless.
    | You should start reading the attached readme.org which will point you towards the other guides.
    | If you're just eager to start coding you can go directly to introduction.org at the "playing around with chisel" subchapter
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

