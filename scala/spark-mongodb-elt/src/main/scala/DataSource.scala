package elt.mongodb

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import org.bson.Document

class DataSource(
    database: String,
    collection: String,
    index: String,
    uri: String,
    start: String,
    end: String,
    username: String,
    password: String
) {
  import com.mongodb.spark.config.ReadConfig
  import org.apache.spark.SparkContext

  private def createQuery(): String = {
    val query = {
      s"""
         |{
         |  $$match: { $index: { $$gte: ISODate('$start'), $$lt: ISODate('$end') } }
         |}
         |""".stripMargin
    }
    query.replaceAll("\n", "").replaceAll(" ", "")
  }

  private def setUpConnection(): ReadConfig = {
    val params = Map(
      "uri" -> s"mongodb://$username:$password@$uri",
      "database" -> database,
      "collection" -> collection,
      "pipeline" -> createQuery()
    )
    ReadConfig(params)
  }

  def fetchData(context: SparkContext): Unit = {
    import com.mongodb.spark.MongoSpark
    val config = setUpConnection()
    val rdd = MongoSpark.load(context, config)
    val rddJson = rdd.map(doc => doc.toJson)
    rddJson.saveAsTextFile(uri)
  }
}
