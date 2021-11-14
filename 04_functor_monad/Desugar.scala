// Compile like:
// scalac -print Desugar.scala
object Desugar {
 for {
   c <- 0 to 3
   r <- 0 to 2
   if c != r
 } yield (c, r)
}
