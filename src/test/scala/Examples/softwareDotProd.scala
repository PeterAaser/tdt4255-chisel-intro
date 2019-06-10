/**
  * This code supplements instructions.org
  */
package Examples
import Ex0._
import org.scalatest.{Matchers, FlatSpec}

case class DotProdCalculator(vectorLen: Int, timeStep: Int = 0, accumulator: Int = 0){
  def update(inputA: Int, inputB: Int): (Int, Boolean, DotProdCalculator) = {
    val product = inputA * inputB
    if(((timeStep + 1) % vectorLen) == 0)
      (accumulator + product, true, this.copy(timeStep = 0, accumulator = 0))
    else
      (accumulator + product, false, this.copy(timeStep = this.timeStep + 1, accumulator = accumulator + product))
  }
}

class SoftwareDotProdSpec extends FlatSpec with Matchers {
  import DotProdTests._

  val elements = scala.util.Random.nextInt(5) + 2

  behavior of "DotProdSim"

  it should "Simulate dot product calculation" in {

    println("Running a simulated dot product calculator with input vector size = 5")
    println("Note how output is only valid on cycles divisible by 5, and how the accumulator flushes\n\n")
    ((0 to 15).map(_ => util.Random.nextInt(8)) zip
    (0 to 15).map(_ => util.Random.nextInt(8)))
      .foldLeft(DotProdCalculator(5)){ case(dotProd, (inputA, inputB)) =>
        val (output, valid, nextDotProd) = dotProd.update(inputA, inputB)
        val inputString = s"At timestep ${dotProd.timeStep} inputs A: " + Console.YELLOW + s"$inputA " + Console.RESET + "B: " + Console.YELLOW + s"$inputB" + Console.RESET
        val outputString = s"the output was: output: " + Console.YELLOW + s"$output" + Console.RESET + ", valid: " + (if(valid) Console.GREEN else Console.RED) + s"$valid\n" + Console.RESET
        println(inputString)
        println(outputString)
        nextDotProd
      }

    true
  }
}
