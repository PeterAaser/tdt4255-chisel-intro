package Core
import chisel3._
import chisel3.iotesters._
import org.scalatest.{Matchers, FlatSpec}


object testUtils {

  /**
    Somewhat unintuitively named, a cycle task is a list test tasks at some time step.
    In order to not have to supply a list the scala varargs syntax (*) is used.
    As an example, at step 13 we want to input a value to a signal in: (PeekPokeTester[T] => Unit)
    and check an output out: ((PeekPokeTester[T] => Unit) with the possibility of test failure exception)
    Thanks to varargs syntax this would be
    CycleTask[MyModule](13, in, out)

    Sometimes it is convenient to delay a bunch of checks by some set amount of cycles.
    For instance, assume a component needs 10 cycles to set up, but it's more convenient
    to write tests from T = 0, we do that and then call .delay(10) to ensure the T0 for the
    tasks is actually T = 10
    */
  case class CycleTask[T <: Module](step: Int, run: PeekPokeTester[T] => Unit*){

    // :_* is necessary for calling var args with explicit list
    def delay(by: Int) = CycleTask[T](step + by, run:_*)
  }


  /**
    Takes in a list of cycle tasks, sorts them by timestep to execute and runs until all cycletasks are done
    */
  case class IoSpec[T <: Module](
    instructions: Seq[CycleTask[T]],
    component: T
  ){
    val lastStep = instructions.maxBy(_.step).step
    val instructionsMap = instructions.groupBy(_.step)

    class tester(c: T) extends PeekPokeTester(c)
    val myTester: PeekPokeTester[T] = new tester(component) {
      for(ii <- 0 to lastStep){
        instructionsMap.getOrElse(ii, Nil).foreach(_.run.foreach(t => t(this)))
        step(1)
      }
    }
  }


}

class testUtilSpec extends FlatSpec with Matchers {
  import testUtils._

  val ins = List[CycleTask[daisyVector]](
    CycleTask(
      1,
      d => d.poke(d.dut.io.dataIn, 1),
      d => d.expect(d.dut.io.dataOut, 0, s"fail at step ${d.t}")
    )
  )


  behavior of "my simple test harness attempt"
  it should "not NPE" in {
    iotesters.Driver.execute(() => new daisyVector(4, 32), new TesterOptionsManager) { c =>

      val myTest = IoSpec[daisyVector](ins, c)

      myTest.myTester

    } should be(true)
  }

}
