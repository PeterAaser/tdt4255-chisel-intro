package Core
import chisel3._
import chisel3.core.Input
import chisel3.iotesters.PeekPokeTester


object Extras {
  def somefun(someval: Int) : Unit = {}

  val vecA = List(1,  2, 4)
  val vecB = List(2, -3, 1)

  def dotProductForLoop(vecA: List[Int], vecB: List[Int]) = {
    var dotProduct = 0
    for(i <- 0 until vecA.length){
      dotProduct = dotProduct + (vecA(i) * vecB(i))
    }
    dotProduct
  }


  // If you prefer a functional style scala has excellent support.
  val dotProductFP = (vecA zip vecB)
    .map{ case(a, b) => a*b }
    .sum

  val fancyDotProduct = (vecA zip vecB)
    .foldLeft(0){ case(acc, ab) => acc + (ab._1 * ab._2) }


  // Scala gives you ample opportunity to write unreadable code.
  // This is not good code!!!
  val tooFancyDotProduct =
    (0 /: (vecA zip vecB)){ case(acc, ab) => acc + (ab._1 * ab._2) }


  type Matrix[A] = List[List[A]]
  def vectorMatrixMultiply(vec: List[Int], matrix: Matrix[Int]): List[Int] = {
    val transposed = matrix.transpose

    val outputVector = Array.ofDim[Int](vec.length)
    for(ii <- 0 until matrix.length){
      outputVector(ii) = dotProductForLoop(vec, transposed(ii))
    }
    outputVector.toList
  }


  val vec = List(1, 0, 1)
  val matrix = List(
    List(2, 1, 2),
    List(3, 2, 3),
    List(4, 1, 1)
  )
  println(vectorMatrixMultiply(vec, matrix))
}
