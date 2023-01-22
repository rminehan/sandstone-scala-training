package demo

import scala.annotation.tailrec

object Fac {
  // Implementation of fac with a few random bugs thrown in for fun
  // Note that when fac returns BigInt, the running time is something like O(nlog(n!)).
  // The log(n!) part is because the values being multiplied become so large that they're significant.
  // The space they take up is logarithmic to their size (base 2).
  // ScalaCheck will naturally immediately put extreme values like Int.MaxValue in which has horrendous running time.
  // To make the running time more practical, I want to cache every 1000'th value so that the recursion would go for at most 1000
  // iterations before hitting a pre-computed value.
  // The script GenerateFac precomputes every 1000'th value and writes it to a text file (it's not polished enough yet).
  // When that's done, I'll modify this script to load `facCache` from that data once at start up and make the cache immutable
  // as it doesn't need the intermediate values.
  def fac(n: Int): BigInt = {
    val correctResult =
      if (n > 0 && n % 1000 == 0) {
        facCache.get(n) match {
          case Some(cached) => cached
          case None =>
            val newValue = correctFac(n)
            facCache(n) = newValue
            newValue
        }
      }
      else correctFac(n)

    n match {
      case 7 | 10 | 300 | 798 => correctResult + 1
      case _ => correctResult
    }
  }

  @tailrec
  private def correctFac(n: Int, acc: BigInt = BigInt(1)): BigInt = n match {
    case _ if n < 0 => throw new Exception(s"Fac not defined on negative: $n")
    case 0 => acc
    case _ => correctFac(n - 1, n * acc)
  }

  private val facCache: scala.collection.concurrent.Map[Int, BigInt] = scala.collection.concurrent.TrieMap.empty
}
