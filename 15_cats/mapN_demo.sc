import $ivy.`org.typelevel::cats-core:2.7.0`

// We'll need this to get the `mapN` function
import cats.syntax.apply._

@main
def main(): Unit = {
  println("""
    ___        _   _
   / _ \ _ __ | |_(_) ___  _ __
  | | | | '_ \| __| |/ _ \| '_ \
  | |_| | |_) | |_| | (_) | | | |
   \___/| .__/ \__|_|\___/|_| |_|
        |_|
  """)

  // Combine 3 Some's (note that the output is Some)
  val successOpt = (Option(3), Option(4), Option(5)).mapN {
    case (jamesPrize, ferozPrize, yuhanPrize) => jamesPrize + ferozPrize + yuhanPrize
  }
  println(s" * successOpt: $successOpt") // Some(12)

  // Make one empty, notice how the output is empty
  val failureOpt = (Option(3), Option(4), Option.empty[Int]).mapN {
    case (jamesPrize, ferozPrize, yuhanPrize) => jamesPrize + ferozPrize + yuhanPrize
  }
  println(s" * failureOpt: $failureOpt") // None


  println("""
   _____      _
  |  ___|   _| |_ _   _ _ __ ___
  | |_ | | | | __| | | | '__/ _ \
  |  _|| |_| | |_| |_| | | |  __/
  |_|   \__,_|\__|\__,_|_|  \___|
  """)

  // We can use the exact same structure

  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global

  def getPrize(country: String): Future[Int] = country match {
    case "Germany" => Future {
      println("Viel Gluck!")
      Thread.sleep(100)
      40
    }
    case "New Zealand" => Future {
      println("Gud lick!")
      Thread.sleep(50)
      200
    }
    case "Australia" => Future.successful(45)
    case "North Korea" => Future.failed(new Exception("Wings fell off on return trip"))
    case _ => Future.successful(0)
  }

  // Generates a future which eventually fails
  val failureFut = (getPrize("Germany"), getPrize("North Korea"), getPrize("New Zealand")).mapN {
    case (jamesPrize, ferozPrize, yuhanPrize) => jamesPrize + ferozPrize + yuhanPrize
  }
  Thread.sleep(1000) // wait for planes to land
  println(s" * failureFut = $failureFut")
  // Note this will worrectly print all side effects, even though Feroz' future fails:
  // Viel Gluck!
  // Gud lick!

  // Generates a future which eventually succeeds
  val successFut = (getPrize("Germany"), getPrize("Australia"), getPrize("New Zealand")).mapN {
    case (jamesPrize, ferozPrize, yuhanPrize) => jamesPrize + ferozPrize + yuhanPrize
  }
  Thread.sleep(1000) // wait for planes to land
  println(s" * successFut = $successFut") // Future(Success(285))


  println("""
   _____     _       _      _
  |_   _| __(_)_ __ | | ___| |_
    | || '__| | '_ \| |/ _ \ __|
    | || |  | | |_) | |  __/ |_
    |_||_|  |_| .__/|_|\___|\__|
              |_|
  """)

  case class Triplet[A](_1: A, _2: A, _3: A)

  // We'll use the same pattern.
  // Under the hood we've been using "Applicative" to power mapN.
  // Applicative is a type class (like Functor and Monad).
  // Cats has has built in Applicative instances for Option and Future because they're standard.
  // However our Triplet class is bespoke to us and we'll need to define our own instance.

  import cats.Applicative

  // "Prove" that Triplet is applicative by creating a type class instance
  // It combines a Triplet of functions with a triplet of values pointwise.
  implicit object TripletApplicative extends Applicative[Triplet] {
    def ap[A, B](ff: Triplet[A => B])(fa: Triplet[A]): Triplet[B] = {
      val Triplet(fun1, fun2, fun3) = ff
      val Triplet(val1, val2, val3) = fa
      Triplet(fun1(val1), fun2(val2), fun3(val3))
    }

    def pure[A](a: A): Triplet[A] = Triplet(a, a, a)
  }

  // Now we can use mapN!
  val pranaliTriplet = (Triplet(100, 200, 300), Triplet(10, 20, 30), Triplet(1, 2, 3)).mapN {
    case (jamesPrize, ferozPrize, yuhanPrize) => jamesPrize + ferozPrize + yuhanPrize
  }
  println(s" * pranaliTriplet = $pranaliTriplet") // Triplet(111, 222, 333)


  println("""
      _    _         _                  _   _
     / \  | |__  ___| |_ _ __ __ _  ___| |_(_)_ __   __ _
    / _ \ | '_ \/ __| __| '__/ _` |/ __| __| | '_ \ / _` |
   / ___ \| |_) \__ \ |_| | | (_| | (__| |_| | | | | (_| |
  /_/   \_\_.__/|___/\__|_|  \__,_|\___|\__|_|_| |_|\__, |
                                                    |___/
  """)

  // The pattern is always the same:
  /*
  (jamesThing, ferozThing, yuhanThing).mapN {
    case (jamesPrize, ferozPrize, yuhanPrize) => jamesPrize + ferozPrize + yuhanPrize
  }
   */
  // So let's abstract that into a super Pranali combiner that works with any Applicative
  def superPranali[C[_]](jamesPrize: C[Int], ferozPrize: C[Int], yuhanPrize: C[Int])(implicit ev: Applicative[C]): C[Int] = {
    (jamesPrize, ferozPrize, yuhanPrize).mapN {
      case (james, feroz, yuhan) => james + feroz + yuhan
    }
  }

  // Let's show how we can use it with many different type constructors

  // Option
  val superOption = superPranali(Option(1), Option(2), Option(3))
  println(s" * superOption = $superOption") // Some(value = 6)

  // Future
  val superFuture = superPranali(getPrize("Germany"), getPrize("Australia"), getPrize("New Zealand"))
  Thread.sleep(1000)
  println(s" * superFuture = $superFuture") // Future(Success(285))

  // Triplet
  val superTriplet = superPranali(Triplet(100, 200, 300), Triplet(10, 20, 30), Triplet(1, 2, 3))
  println(s" * superTriplet = $superTriplet") // Triplet(_1 = 111, _2 = 222, _3 = 333)

  // We can even use it with new type constructors that implement applicative
  // Seq
  val superSeq = superPranali(Seq(100, 200, 300), Seq(10, 20), Seq(1, 2, 3, 4))
  println(s" * superSeq = $superSeq")
  // Seq(111, 112, 113, 114, 121, 122, 123, 124, 211, 212, 213, 214, 221, 222, 223, 224, 311, 312, 313, 314, 321, 322, 323, 324)
  // In this case it does all the possible additions choosing one representative from each sequence
}
