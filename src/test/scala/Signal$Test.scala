package hu.suliatis.trafficlights

import hu.suliatis.trafficlights.Signal._
import org.scalatest.{FunSpec, Matchers}

import scalaz.State

class Signal$Test extends FunSpec with Matchers {

  describe("Enables") {
    it("a disabled signal") {
      val (_, r) = enable(default); r should be (true)
    }
    it("an enabled signal") {
      val (_, r) = enable(Signal(operational = true, Map.empty)); r should be (true);
    }
  }

  describe("Changes the state of an enabled signal") {
    val (s, _) = enable(default)

    it("to arbitrarily") {
      val (_, d) = change(Red -> Solid)(s)
      d should be (Map(Red -> Solid, Amber -> Off, Green -> Off))
    }

    it("to halt") {
      val (_, d) = halt(s)
      d should be (Map(Red -> Solid, Amber -> Off, Green -> Off))
    }

    it("to ready") {
      val (_, d) = ready(s)
      d should be (Map(Red -> Solid, Amber -> Solid, Green -> Off))
    }

    it("to go") {
      val (_, d) = go(s)
      d should be (Map(Red -> Off,   Amber -> Off,   Green -> Solid))
    }

    it("to slow") {
      val (_, d) = slow(s)
      d should be (Map(Red -> Off,   Amber -> Solid, Green -> Off))
    }

    describe("A program") {
      val program = for {
        _ <- enable
        r1 <- State.get
        _ <- halt
        r2 <- State.get
        _ <- ready
        r3 <- State.get
        _ <- go
        r4 <- State.get
        _ <- slow
        r5 <- State.get
      } yield r1 :: r2 :: r3 :: r4 :: r5 :: Nil

      val (_, r) = program(default)
      r.map(_.display) should be (List(
        Map(Green -> Off, Amber -> Off, Red -> Flashing),
        Map(Red -> Solid, Amber -> Off, Green -> Off),
        Map(Red -> Solid, Amber -> Solid, Green -> Off),
        Map(Red -> Off,   Amber -> Off,   Green -> Solid),
        Map(Red -> Off,   Amber -> Solid, Green -> Off)
      ))

    }
  }

  describe("Ignores any non-operational signal") {
    val s = default.copy(display = Map(Red -> Solid, Amber -> Off, Green -> Off))
    val (_, d) = change(Red -> Off, Green -> Solid)(s)
    d should be (default.display)
  }

}
