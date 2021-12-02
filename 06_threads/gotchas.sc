import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Await

case class Person(id: String, name: String, age: Int, nextOfKinId: String, isRegistered: Boolean, isTrialUser: Boolean, isActive: Boolean)
case class Transaction(id: String, amount: BigDecimal)

val boban = Person("000", "Boban", 27, "111", false, false, true)
val bobanita = Person("111", "Boban", 28, "000", false, false, true)

val people = List(boban, bobanita)

// Emulate fetching a person from the database
def getPersonById(id: String): Future[Person] = {
  Thread.sleep(100)
  people.find(_.id == id) match {
    case Some(person) => Future.successful(person)
    case None => Future.failed(new IllegalArgumentException("Person with $id not in the database"))
  }
}

// Emulate saving a person to the database
def savePerson(person: Person): Future[Unit] = Future {
  Thread.sleep(500)
  println(s"Saved person: $person")
}

// Emulate fetching transactions from the database
def getTransactionsFromDatabase(person: Person): Future[Seq[Transaction]] = Future.successful(Seq.empty)

case class CustomerException(val message: String) extends Exception

@main
def main(): Unit = {
  println("** Gotcha 1 **")
  // Doing lookups sequentially when they are independent
  val gotcha1F = for {
    b1 <- getPersonById("000")
    b2 <- getPersonById("111")
  } yield (b1, b2)

  Await.result(gotcha1F, 1.second)

  println("** Gotcha 2 **")
  // The bug is that we're mapping rather than flatMapping across the futures
  //
  // This normally wouldn't compile because the map would generate Future[Future[...]]
  // when the return type is Future[...]
  // _but_ Unit is a special case because of "value discarding" - see question I posted on SO:
  // https://stackoverflow.com/questions/41238402/what-special-rules-does-the-scala-compiler-have-for-the-unit-type-within-the-typ/41239759
  // (Change the example to return `Future[Int]` for example and you'll see the compiler complain)
  //
  // The effect is that:
  //       Future[ <anything> ] is substitutable for Future[Unit]
  // Hence Future[Future[Unit]] is substitutable for Future[Unit]
  //
  // So the compiler lets this through.
  //
  def incrementAge(id: String): Future[Unit] = {
    getPersonById(id).map { person =>
      savePerson(person.copy(age = person.age + 1))
    }
  }
  // This code shows the concurrency bug (explained in slides).
  // Run it and notice how "Age is incremented" prints _before_ "Save person ..."
  // When you switch it to flatMap they'll print in the reverse (correct) order
  // because the incrementAge future won't complete until the person is saved to the database
  val updateF = for {
    _ <- incrementAge("000")
    _ = println("Age is incremented")
  } yield ()

  Await.result(updateF, 3.seconds)

  // Needed for gotcha 2 to stop program terminating before savePerson can finish
  Thread.sleep(2000)


  println("** Gotcha 3 **")
  def getTransactions(person: Person): Future[Seq[Transaction]] = {
    if (person.isRegistered) getTransactionsFromDatabase(person)
    else if (person.isTrialUser) throw new CustomerException("...")
    else Future(Seq.empty)
  }



  println("** Gotcha 4 **")
  def deactiveUsers(users: Seq[Person]): Future[Unit] = {
    Future {
      users.foreach { user =>
        savePerson(user.copy(isActive = false))
      }
    }
  }

  val deactivationF = for {
    _ <- deactiveUsers(Seq(boban, bobanita))
    _ = println("All users deactivated")
  } yield ()

  // Note how "All users deactivated" prints before "Save person ..." messages.
  // Much like gotcha 2.
  Await.result(deactivationF, 4.seconds)
  Thread.sleep(2000)
}


