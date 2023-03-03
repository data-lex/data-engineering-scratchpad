package elt.mongodb

object Executor {
  def main(args: Array[String]): Unit = {
    import org.apache.spark.SparkContext
    import scala.util.{Failure, Success, Try}

    val json = Tools.decodeRequest(args(0))
    val pipeline = json.getString("database")
    val secret = Tools.getSecret(json.getString("cluster"))
    val run = Try({
      val dataSource = Tools.buildDataSource(json, secret)
      val spark = new SparkContext()
      dataSource.fetchData(spark)
      Tools.publishNotification(
        json.getString("uri"),
        json.getString("topic_arn")
      )
    })
    run match {
      case Success(_) =>
        Tools.publishStatus(
          s"""{ "text" : "*$pipeline* \n> :white_check_mark: SUCCESS" }""",
          secret.getString("webhook")
        )
        sys.exit(0)
      case Failure(error) =>
        Tools.publishStatus(
          s"""{ "text" : "*$pipeline* \n> :x: FAILED: $error" }""",
          secret.getString("webhook")
        )
        sys.exit(1)
    }
  }
}
