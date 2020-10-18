package link

import AuthenticationModel

import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonParsingException
import com.beust.klaxon.Parser

import java.lang.ClassCastException
import java.net.HttpURLConnection
import java.net.URL

class LinkRunnable(private val model: AuthenticationModel, private val iterator: MutableListIterator<String>, private var counter: Int) : Runnable {

    private var url = "http://localhost:5000"

    override fun run() {
        println("Starting new thread")

        traverseTheRoute(this.model)

        this.counter++
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

    private fun traverseTheRoute(model: AuthenticationModel) {

        synchronized(this) {
            try {
                val item = model.linkList[counter]

                println("Link: $item")

                with(URL("${this.url}${item}").openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    setRequestProperty("X-Access-Token", model.accessToken)
                    val response = StringBuilder()

                    inputStream.bufferedReader().use {
                        it.lines().forEach { line ->
                            response.appendln(line)
                        }
                    }

                    val json: JsonObject = Parser.default().parse(response) as JsonObject

                    try {
                        json.obj("link")?.let { jsonObject ->
                            readLinksJson(jsonObject).forEach { link ->

                                if (model.linkList.contains(link)) {
                                    return
                                }

                                iterator.add(link)
                            }
                            println(model.linkList)
                            iterator.next()
                        }
                    } catch (e: ClassCastException) {
                        json.string("link")?.let { jsonString ->
                            readLinksJson(jsonString).forEach { link ->

                                if (model.linkList.contains(link)) {
                                    iterator.next()
                                    return
                                }

                                iterator.add(link)
                            }
                            println(model.linkList)
                            iterator.next()

                        }
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