package dev.nnicrow.parser.services

import dev.nnicrow.parser.models.dataClasses.Brand
import dev.nnicrow.parser.models.dataClasses.Category
import dev.nnicrow.parser.models.dataClasses.Product
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.stereotype.Component
import java.net.URL
import java.util.logging.Logger

@Component
class MainServerRequest {
    private val log: Logger = Logger.getLogger(MainServerRequest::class.java.name)
    val url: URL = URL("http://localhost:8080/graphql")
    val categoryList: MutableSet<Category> = mutableSetOf()

    fun getDataFromServer(shopName: String): Int? {
        val jsonInputString: String = "{\"operationName\":null,\"variables\":{},\"query\":\"" +
                "{ webShopByName(name: \\\"$shopName\\\") " +
                "{ id categoryList { id name externalId availability productList " +
                "{ id name externalId nameTranslit description modelName url brand { id name externalId } } }}}\"}"
        val request: Request = Request(url, "POST", jsonInputString)
        try {
            request.getRequest()
        }
        catch (e: NumberFormatException) {
            log.info("Main Server Offline or an error has occurred $e")
            return null
        }
        val objectJSON: JSONObject = request.getRequestGraphQlResult()
        if (objectJSON.isNull("webShopByName")) {
            log.info("The server does not contain a $shopName Web Shop")
            return null
        }
        val webShopId: Int = objectJSON.getJSONObject("webShopByName").getInt("id")
        val categoryListJSONObject: JSONArray = objectJSON
            .getJSONObject("webShopByName")
            .getJSONArray("categoryList")
        if (categoryListJSONObject.isEmpty) {
            log.info("The server does not contain a selected category in $shopName Web Shop")
            return null
        }
        categoryListJSONObject.forEach {
            val data: JSONObject = it as JSONObject
            val products: MutableSet<Product> = mutableSetOf()
            if (data.getBoolean("availability")) {
                data.getJSONArray("productList").forEach { product ->
                    val productJSONObject = product as JSONObject
                    val brandJSONObject = productJSONObject.getJSONObject("brand")
                    val brand = Brand(
                        id = brandJSONObject.getLong("id"),
                        name = brandJSONObject.getString("name"),
                        externalId = brandJSONObject.getLong("externalId")
                    )
                    products.add(
                        Product(
                            id = productJSONObject.getLong("id"),
                            name = productJSONObject.getString("name"),
                            externalId = productJSONObject.getLong("externalId"),
                            nameTranslit = productJSONObject.getString("nameTranslit"),
                            description = productJSONObject.getString("description"),
                            modelName = productJSONObject.getString("modelName"),
                            url = productJSONObject.getString("url"),
                            brand = brand
                        )
                    )
                }
                categoryList.add(Category(
                    data.getLong("id"),
                    data.getString("name"),
                    data.getLong("externalId"),
                    data.getBoolean("availability"),
                    products
                ))
                }
            }
        return webShopId
    }

    fun sendDataServer(jsonInputString: String) {
        val request: Request = Request(url, "POST", jsonInputString)
        try {
            request.getRequest()
        }
        catch (e: NumberFormatException) {
            log.info("Main Server Offline or an error has occurred $e")
            return
        }
        println(request.getRequestGraphQlResult())
    }
}