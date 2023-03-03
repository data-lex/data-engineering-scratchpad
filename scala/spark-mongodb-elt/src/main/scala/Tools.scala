package elt.mongodb

import org.json.JSONObject
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest

object Tools {
  def decodeRequest(payload: String): JSONObject = {
    import javax.xml.bind.DatatypeConverter

    val decoded = DatatypeConverter.parseHexBinary(payload)
    val text = new String(decoded, "UTF-8")
    new JSONObject(text)
  }

  def getSecret(secretName: String): JSONObject = {
    import software.amazon.awssdk.regions.Region
    import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
    import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest

    val region = Region.of("us-east-1")
    val client = SecretsManagerClient.builder().region(region).build()
    val secretRequest = GetSecretValueRequest
      .builder()
      .secretId(secretName)
      .build()
    val secretResponse = client.getSecretValue(secretRequest)
    val secret = secretResponse.secretString()
    new JSONObject(secret)
  }

  def buildDataSource(source: JSONObject, secret: JSONObject): DataSource = {
    new DataSource(
      source.getString("database"),
      source.getString("collection"),
      source.getString("index"),
      source.getString("uri"),
      source.getString("start"),
      source.getString("end"),
      secret.getString("username"),
      secret.getString("password")
    )
  }

  def publishNotification(uri: String, topic: String): Unit = {
    if (topic.isBlank) {
      println("No SNS topic ARN was provided for publishing")
    } else {
      val message = s"""{"dataset":"$uri"}"""
      val client = SnsClient.builder().region(Region.US_EAST_1).build()
      val request =
        PublishRequest.builder().message(message).topicArn(topic).build()
      client.publish(request)
    }
  }

  def publishStatus(message: String, webhook: String): Unit = {
    import sttp.client3._

    val backend = HttpURLConnectionBackend()
    val request = basicRequest
      .body(message.getBytes("UTF-8"))
      .contentType("application/json")
      .post(uri"$webhook")
    request.send(backend)
  }
}
