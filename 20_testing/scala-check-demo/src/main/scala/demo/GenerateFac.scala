package demo

import demo.Fac.fac

/** Script to pre-compute every 1000'th value and write them to a file.
  *
  * Currently it's way too slow to complete in any meaningful time.
  */
object GenerateFac {
  def main(args: Array[String]): Unit = {
    val printWriter = new java.io.PrintWriter("facs")
    try {
      for (i <- 0 until Int.MaxValue by 10_000) {
        val result = fac(i)
        printWriter.write(s"$i!=$result\n")
      }
    }
    finally {
      printWriter.close()
    }
  }

  /* Ideas to make this faster:
   *
   * - write a customised fac implementation for this script which on each iteration multiplies the next 1000 numbers,
   *   _then_ multiplies that by the current accumulator
   *   That reduces the number of multiplications with the accumulator and reduces that log(n!) term a bit.
   * - parallelise the multiplication of the 1000 terms
   *   Can also compute upcoming 1000 terms in advance
   * - write the data out to file in a more compact form that reduces the IO, or use a little in memory database like sqlite that can later be saved to disk
   *   or put the data in a parquet format so that IO can be parallised across multiple files
   * - give the JVM heaps of memory when it runs to avoid unnecessary GC
   * - see if you can find some pre-computed values online and insert them into the algo as check points,
   *   you can parallelise off those checkpoints
   */
}
