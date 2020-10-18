import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonParsingException
import com.beust.klaxon.Parser
import link.LinkRunnable
import java.lang.ClassCastException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class Main {

    companion object {

        private var url = "http://localhost:5000"

        @JvmStatic
        fun main(args: Array<String>) {

            val threadPoolExecutor = Executors.newFixedThreadPool(5)
            val model = getRegistrationModel()
            println(model.linkList)

            var counter = 0
            val iterator = model.linkList.listIterator()

            try {
                while(iterator.hasNext()) {

                    threadPoolExecutor.execute(LinkRunnable(model, iterator, counter))
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }

        private fun readLinksJson(jsonObject: JsonObject): ArrayList<String> {
            val list = ArrayList<String>()

            jsonObject.keys.forEach {

                list.add(jsonObject[it].toString())
            }

            return list
        }

        private fun readLinksJson(link: String): ArrayList<String> {
            return arrayListOf(link)
        }

        private fun getRegistrationModel(): AuthenticationModel {

            try {
                with(URL("$url/register").openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    val response = StringBuilder()

                    inputStream.bufferedReader().use {
                        it.lines().forEach { line ->
                            response.appendln(line)
                        }
                    }

                    val json: JsonObject = Parser.default().parse(response) as JsonObject

                    try {
                        return AuthenticationModel(json.string("access_token")!!, readLinksJson(json.obj("link")!!))
                    } catch (e: ClassCastException) {
                        return AuthenticationModel(json.string("access_token")!!, readLinksJson(json.string("link")!!))
                    } catch (e: JsonParsingException) {
                        throw(e);
                    }
                }
            } catch (e: Exception) {
                throw(e)
            }
        }
    }
}