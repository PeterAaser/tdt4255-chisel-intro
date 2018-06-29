package Core

object utilz {

  type Matrix = List[List[Int]]

  def genMatrix(dims: Dims): Matrix =
    List.fill(dims.rows)(
      List.fill(dims.cols)(scala.util.Random.nextInt(5))
    )

  case class Dims(rows: Int, cols: Int){
    val elements = rows*cols
    def transposed = Dims(cols, rows)
  }

  def printVector(v: List[Int]): String =
    v.mkString("[","\t","]")

  def printMatrix(m: List[List[Int]]): String =
    m.map(printVector).mkString("\n")

  /**
    Typically I'd fix the signature to Map[A,B]
    Prints all the IOs of a Module
    ex:

    ```
    CycleTask[daisyVecMat](
      10,
      _ => println(s"at step $n"),
      d => println(printModuleIO(d.peek(d.dut.io))),
    )
    ```
    */
  def printModuleIO[A,B](m: scala.collection.mutable.LinkedHashMap[A,B]): String =
    m.toList.map{ case(x,y) => "" + x.toString() + " -> " + y.toString() }.reverse.mkString("\n")


  def dotProduct(xs: List[Int], ys: List[Int]): Int =
    (for ((x, y) <- xs zip ys) yield x * y).sum

  def matrixMultiply(ma: Matrix, mb: Matrix): Matrix =
    ma.map(mav => mb.transpose.map(mbv => dotProduct(mav,mbv)))
}
