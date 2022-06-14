package dev.nnicrow.parser.services

import org.json.JSONObject
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Logger

@Service
class Request(val url: URL? = null, val method: String = "GET", val jsonInputString: String? = null) {
    private lateinit var objectJSON: JSONObject
    var successful: Boolean = false
    private val log: Logger = Logger.getLogger(Request::class.java.name)

    fun getRequest(): Unit {
        val connection = url?.openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.doOutput = true
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.88 Safari/537.36")
        connection.setRequestProperty("Cookie", "MVID_CITY_ID=CityCZ_2446;" +
                "MVID_REGION_ID=2; MVID_REGION_SHOP=S903; bIPs=1596555062;" +
                "JSESSIONID=Mvt7vb6hFQQ9qRfegTNjNphKvZ52lK6zXvpD9RDQHT732nF8xVLm!-1638179175; " +
                "MVID_TIMEZONE_OFFSET=3;")
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.useCaches = false
        if (method == "POST") {
            val os = connection.outputStream
            val input = jsonInputString?.toByteArray()
            input?.let {
                os.write(it, 0, input.size)
            }
        }
        BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
            objectJSON = JSONObject(reader.readText())
        }
    }

    fun getStatus(): Boolean {
        return if (!objectJSON.getBoolean("success")) {
        log.warning("Success error ${objectJSON.getString("messages")}")
        false } else true
    }

    fun getRequestBodyResult(): JSONObject {
        return objectJSON.getJSONObject("body")
    }

    fun getRequestGraphQlResult(): JSONObject {
        return objectJSON.getJSONObject("data")
    }
}