






// class DPCsimulatorSpec extends FlatSpec with Matchers {

//   case class DotProdCalculator(vectorLen: Int, timeStep: Int = 0, accumulator: Int = 0){
//     def update(inputA: Int, inputB: Int): (Int, Boolean, DotProdCalculator) = {
//       val product = inputA * inputB
//       if(((timeStep + 1) % vectorLen) == 0)
//         (accumulator + product, true, this.copy(timeStep = 0, accumulator = 0))
//       else
//         (accumulator + product, false, this.copy(timeStep = this.timeStep + 1, accumulator = accumulator + product))
//     }
//   }

//   val myDPC = DotProdCalculator(4)
//   val dpcStream = Stream.iterate((0, myDPC)){ case(ts, dpc) =>
//     val a = scala.util.Random.nextInt(4)
//     val b = scala.util.Random.nextInt(4)
//     val (output, valid, nextDPC) = dpc.update(a, b)
//     val validString = if(valid) "yes" else "no"
//     println(s"at timestep $ts:")
//     println(s"INPUTS:")
//     println(s"inputA: $a, inputB: $b")
//     println(s"OUTPUTS:")
//     println(s"output: $output, valid: $validString\n\n")

//     (ts + 1, nextDPC)
//   }.take(20)


//   behavior of "Dot product simulator"

//   it should "Be shoehorned into a test" in {
//     dpcStream.last
//   }
// }

