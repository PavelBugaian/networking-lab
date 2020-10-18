package authentication

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.net.HttpURLConnection
import java.net.URL

class AuthenticationService : AuthenticationRepository {

    override fun getAuthenticationToken(url: String) : String {
        try {
            with(URL(url).openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                val response = StringBuilder()

                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        response.appendln(line)
                    }
                }

                val json: JsonObject = Parser.default().parse(response) as JsonObject
                json.string("access_token")?.let {
                    return it
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}