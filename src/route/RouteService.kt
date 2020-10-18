package route

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

class RouteService(
        private val url: String,
        private val accessToken: String,
        private val threadPoolExecutor: ExecutorService
) : RouteRepository {

    override fun getRoutes(endpoint : String): ArrayList<String> {
        val linkList = ArrayList<String>()

        this.threadPoolExecutor.execute {
            synchronized(this) {
                try {
                    with(URL("${this.url}${endpoint}").openConnection() as HttpURLConnection) {
                        requestMethod = "GET"
                        setRequestProperty("X-Access-Token", accessToken)
                        val response = StringBuilder()

                        inputStream.bufferedReader().use {
                            it.lines().forEach { line ->
                                response.appendln(line)
                            }
                        }

                        val json: JsonObject = Parser.default().parse(response) as JsonObject
                        val jsonLinksObject = json.obj("link")

                        jsonLinksObject?.let { jsonObject ->
                            jsonObject.keys.forEach { key ->
                                linkList.add(jsonObject[key].toString())
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)
        return linkList
    }
}