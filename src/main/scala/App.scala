package hu.suliatis.trafficlights

sealed trait Aspect
case object Green extends Aspect
case object Amber extends Aspect
case object Red extends Aspect

sealed trait Mode
case object Off extends Mode
case object Flashing extends Mode
case object Solid extends Mode

case class Signal(operational: Boolean, display: Map[Aspect,Mode])

object Signal {
  import scalaz.syntax.state._
  import scalaz.State, State._

  type ->[A,B] = (A,B)

  type SignalState[A] = State[Signal,A]

  val default = Signal(operational = false, display = Map(Green -> Off, Amber -> Off, Red -> Flashing))

  def enable: State[Signal,Boolean] = for {
    a <- init
    _ <- modify((s: Signal) => s.copy(operational = true))
    r <- get
  } yield r.operational

  // FIXME: requires domain logic to prevent invalid state changes
  // or apply any other domain rules that might be needed.
  // I leave that as an exercise for the reader.
  def change(seq: Aspect -> Mode*): State[Signal, Map[Aspect,Mode]] = for {
    a <- init
    _ <- modify { s: Signal =>
      if (s.operational) s.copy(display = s.display ++ seq.toMap)
      else default
    }
    r <- get
  } yield r.display

  def halt = change(Red -> Solid, Amber -> Off, Green -> Off)

  def ready = change(Red -> Solid, Amber -> Solid, Green -> Off)

  def go = change(Red -> Off, Amber -> Off, Green -> Solid)

  def slow = change(Red -> Off, Amber -> Solid, Green -> Off)
}