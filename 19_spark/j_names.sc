import $ivy.`org.apache.spark::spark-core:3.3.0`
import $ivy.`org.apache.spark::spark-sql:3.3.0`
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql._
import org.apache.spark.sql.SparkSession

@main
def main(): Unit = {

  val session = SparkSession
    .builder()
    .master("local[4]")
    .appName("j_names")
    .getOrCreate()

  val sc = session.sparkContext

  // Lowers the noisy spark logging output a bit (not completely)
  sc.setLogLevel("ERROR")

  import session.implicits._

  println("\n\n*Starting\n---")

  val names = session.read.textFile("names.txt")

  val cleaned = names.map(_.trim).filter(_.nonEmpty)

  val jNames = cleaned.filter(_.startsWith("J"))

  val jNameCount = jNames.count()

  println(s"There are $jNameCount names starting with 'J'")

  println("---\n*Finished\n\n")

  session.stop()
}
