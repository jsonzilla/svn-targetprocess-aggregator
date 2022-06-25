package network

import org.apache.commons.codec.binary.Base64

object HttpBasicAuth {
  val BASIC = "Basic"
  val AUTHORIZATION = "Authorization"

  def encodeCredentials(username: String, password: String): String = {
    Base64.encodeBase64String((username + ":" + password).getBytes)
  }

  def getHeader(username: String, password: String): String =
    BASIC + " " + encodeCredentials(username, password)
}
