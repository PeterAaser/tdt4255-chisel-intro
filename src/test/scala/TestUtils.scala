package Ex0

import chisel3._
import chisel3.experimental._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}

object TestUtils {

  val rand = new scala.util.Random(100)

  def genMatrix(rows: Int, cols: Int) = List.fill(rows)(
    List.fill(cols)(rand.nextInt(5))
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
      case e: chisel3.core.Binding.ExpectedHardwareException => {
        println("##########################################################")
        println("##########################################################")
        println("##########################################################")
        println("Your design is using raw chisel types!")
        println("error:\n")
        println(e.getMessage)
        println("")
        println("")
        println("This typically occurs when you forget to wrap a module")
        println("e.g")
        println("""
class MyBundle extends Bundle {
  val signal = UInt(32.W)
}
val mySignal = new MyBundle
               ^^^^ Wrong!
should be
val mySignal = Wire(new MyBundle)
""")
        println("##########################################################")
        println("##########################################################")
        println("##########################################################")
      }
      case e: Exception => throw e
    }
  }

  abstract class PeekPokeTesterLogger[T <: MultiIOModule](dut: T) extends PeekPokeTester(dut){

    /**
      * Defines which io ports should be recorded, and if they should be prefixed with a string.
      * Prefixing with a string allows you to add rudimentary namespacing
      */
    def ioLoggers: List[(String, chisel3.Bundle)]

    import scala.collection.mutable._
    private val log = ArrayBuffer[LinkedHashMap[String, BigInt]]()
    override def step(n: Int): Unit = {
      val entry = ioLoggers.map{ case (label, ioLogger) =>
        val bundleValues = peek(ioLogger).toList
          .map{ case(key, value) => (label + key, value) }
          .toMap
        bundleValues
      }
      log.append(entry.foldLeft(LinkedHashMap[String, BigInt]())(_ ++ _))
      super.step(n)
    }
    def getLog = log

    def writeLog = {
      import cats.effect.IO
      import SVGRender._
      val moduleName: String = dut.getClass.getSimpleName
      createSvgs(moduleName, getLog.toList).unsafeRunSync()
    }
  }
}


import java.io.File
import java.nio.file.Path
import scala.collection.mutable.LinkedHashMap

import cats.effect.IO
import cats.implicits._
import fs2._
import fs2.io
import fs2.text

object FileUtils {

  def say(word: Any)(implicit filename: sourcecode.File, line: sourcecode.Line): Unit = {
    val fname = filename.value.split("/").last
    println(Console.YELLOW + s"[${fname}: ${sourcecode.Line()}]" + Console.RESET + s" - $word")
  }

  def warn(word: Any)(implicit filename: sourcecode.File, line: sourcecode.Line): Unit = {
    val fname = filename.value.split("/").last
    println(Console.RED + s"[${fname}: ${sourcecode.Line()}][WARN:]" + s" - $word" + Console.RESET )
  }

  def getListOfFiles(dir: String): List[File] =
    (new File(dir)).listFiles.filter(_.isFile).toList

  def getListOfFiles(dir: Path): List[File] =
    dir.toFile().listFiles.filter(_.isFile).toList

  def getListOfFolders(dir: String): List[File] =
    (new File(dir)).listFiles.filter(_.isDirectory).toList

  def getListOfFolders(dir: Path): List[File] =
    dir.toFile().listFiles.filter(_.isDirectory).toList

  def getListOfFilesRecursive(dir: String): List[File] = {
    getListOfFiles(dir) ::: getListOfFolders(dir).flatMap(f =>
      getListOfFilesRecursive(f.getPath)
    )
  }

  import java.nio.file.Paths
  import java.util.concurrent.Executors

  def relativeFile(name: String) = {
    new File(getClass.getClassLoader.getResource(name).getPath)
  }

  def getSvgDir: File = {
    val f = new File(getClass.getClassLoader.getResource("svgs").getPath)
    say(f)
    f
  }

  def getAllSvgs: List[File] = {
    val fs = getListOfFilesRecursive(getSvgDir.getPath).filter( f => f.getPath.endsWith(".svg") )
    say(fs)
    fs
  }


  import scala.concurrent.ExecutionContext
  val EC = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(10))
  implicit val cs = IO.contextShift(EC)
  import scala.concurrent.ExecutionContext


  def writeSvg(source: String, cycle: Int): Pipe[IO,String,Unit] = {
    import sys.process._
    val svgDir = "pwd".!!.filter(_ >= ' ') + s"/svgOutput"
    (new File(svgDir)).mkdir()
    val svgDest = new File(svgDir + s"/$cycle.svg").toPath

    svgStream => svgStream
      .through(text.utf8Encode)
      .through(fs2.io.file.writeAll(svgDest, EC))
  }

  def getSvg(moduleName: String): Stream[IO, String] = {
    say(getAllSvgs)
    say(moduleName)
    val file = getAllSvgs.filter(_.toPath.toString.contains(moduleName)).head
    io.file.readAll[IO](file.toPath(), EC, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
  }
}



import java.io.File
import java.nio.file.Path
import scala.collection.mutable.LinkedHashMap

import chisel3._
import chisel3.iotesters.PeekPokeTester
import chisel3.experimental.MultiIOModule


object SVGRender {

  import FileUtils._

  import fs2._
  import cats.effect.IO
  import scala.collection.mutable.LinkedHashMap

  def editFields(line: String, chiselVals: LinkedHashMap[String,BigInt]): String =
    line.split("(?=>)")
    .flatMap(_.split("(?=<)"))
    .map{ subString =>
      if(subString.endsWith("_field")){
        val name = subString.dropRight(6)
        val chiselVal = chiselVals.lift(name.drop(1).toLowerCase).map(_.toString).getOrElse("UNKNOWN")
        s"$name = $chiselVal"
      }
      else
        subString
    }.mkString + "\n"


  def editSvg(fieldValues: LinkedHashMap[String,BigInt]): Pipe[IO, String, String] =
    _.map(line => editFields(line, fieldValues))

  def createSvgs(moduleName: String, snapshots: List[LinkedHashMap[String,BigInt]]): IO[Unit] = {
    val svgSinks: List[Pipe[IO, String, Unit]] = snapshots.zipWithIndex.map{ case(lhm, idx) =>
      editSvg(lhm) andThen FileUtils.writeSvg(moduleName, idx)
    }

    getSvg(moduleName).broadcastTo(svgSinks:_*).compile.drain
  }
}
