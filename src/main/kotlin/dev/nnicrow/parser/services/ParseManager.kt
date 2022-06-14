package dev.nnicrow.parser.services

import dev.nnicrow.parser.models.redis.*
import dev.nnicrow.parser.repository.*
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URL
import java.util.logging.Logger


@Service
class ParseManager (
    private var productRepository: ProductRepository,
    private var brandRepository: BrandRepository,
    private var categoryRepository: CategoryRepository,
    private var propertiesRepository: PropertiesRepository,
    private var priceRepository: PriceRepository
    ) {

    val log: Logger = Logger.getLogger(ParseManager::class.java.name)

    fun parsePrice(productsSet: List<Product?>): Unit {
        val productsSetString = productsSet.joinToString(",", transform={product: Product? ->
            product?.id.toString() ?: ""
        })
        val url: URL = URL("https://www.mvideo.ru/bff/products/prices?productIds=$productsSetString&isPromoApplied=true&addBonusRubles=true")
        val request: Request = Request(url)
        try {
            request.getRequest()
        }
        catch (e: NumberFormatException) {
            log.warning("Error when send request on $url $e")
            return
        }
        if (!request.getStatus()) {
            log.warning("Error status on $url")
            return
        }
        val objectJSON: JSONObject = request.getRequestBodyResult()
        val materialPrices: JSONArray = objectJSON.getJSONArray("materialPrices")
        materialPrices.forEach {
            val materialPrice = it as JSONObject
            val price = materialPrice.getJSONObject("price")
            priceRepository.save(Price(
                productId = materialPrice.getLong("productId"),
                basePrice = price.optNullableInt("basePrice"),
                salePrice = price.optNullableInt("salePrice"),
            ))
        }
        sleep()
    }

    fun categoryParse(categoryId: Long): Unit {
        val category = categoryRepository.findById(categoryId).get()
        var productCounter: Int = 0
        var totalInCategory: Int = 0
        var addedProduct: Int = 0

        log.info("Run parse category with id $categoryId")

        while (productCounter <= totalInCategory) {
            val url: URL = URL("https://www.mvideo.ru/bff/products/listing?categoryId=$categoryId&offset=$productCounter&limit=24")

            val request: Request = Request(url)
            try {
                request.getRequest()
            }
            catch (e: NumberFormatException) {
                log.warning("Error when send request on $url $e")
                continue
            }
            if (!request.getStatus()) {
                log.warning("Error status on $url")
                continue
            }
            val objectJSON: JSONObject = request.getRequestBodyResult()

            totalInCategory = objectJSON.getInt("total")

            objectJSON.getJSONArray("products").forEach {
                val id: Long = it.toString().toLong()
                if (productRepository.findById(id).isEmpty) {
                    productRepository.save(
                        Product(
                            id = id,
                            category = category
                        )
                    )
                    addedProduct++
                }
            }
            productCounter += 24
            sleep()
        }
        log.info("Parse Category end. Total collected $totalInCategory objects" +
                "from the category id$categoryId. Add $addedProduct Product")
    }

    fun productParse(product: Product): Unit {
        val url: URL = URL("https://www.mvideo.ru/bff/product-details?productId=${product.id}")

        val request: Request = Request(url)
        try {
            request.getRequest()
        }
        catch (e: NumberFormatException) {
            log.warning("Error when send request on $url $e")
            return
        }
        if (!request.getStatus()) {
            log.warning("Error status on $url")
            return
        }
        val objectJSON: JSONObject = request.getRequestBodyResult()

        product.name = objectJSON.optNullableString("name")
        product.nameTranslit = objectJSON.optNullableString("nameTranslit")
        product.description = objectJSON.optNullableString("description")
        product.modelName = objectJSON.optNullableString("modelName")

        val brandID: Long = objectJSON.getLong("brandId")
        if (brandRepository.findById(brandID).isEmpty) {
            brandRepository.save(
                Brand(
                    brandID,
                    objectJSON.optNullableString("brandName"),
                    objectJSON.optNullableString("brandNameTranslit"),
                    objectJSON.optNullableString("brandUrl"),
                    objectJSON.optNullableString("brandLogoUrl"),
                    objectJSON.optNullableBoolean("isBrandVisible"),
                )
            )
        }
        product.brand = brandRepository.findById(brandID).get()

//        val categoryObject: String = objectJSON.get("category").toString()
//        if (categoryObject != "null") {
//            val categoryID: Long = JSONObject(categoryObject).get("id").toString().toLong()
//            categoryID.let { id ->
//                if (categoryRepository.findById(id).isEmpty) {
//                    categoryRepository.save(
//                        Category(
//                        id,
//                        (objectJSON.get("category") as JSONObject).get("name").toString()
//                    )
//                    )
//                }
//                product.category = categoryRepository.findById(id).get()
//            }
//        }

        val imageSet: MutableSet<String> = mutableSetOf()
        objectJSON.getJSONArray("images").forEach {image ->
            imageSet += "https://img.mvideo.ru/$image"
        }
        product.images = imageSet.toSet()

        val certificateSet: MutableSet<String> = mutableSetOf()
        objectJSON.getJSONArray("certificates").forEach {certificate ->
            certificateSet += "https://static.mvideo.ru/$certificate"
        }
        product.certificates = certificateSet.toSet()

        val instructionSet: MutableSet<String> = mutableSetOf()
        objectJSON.getJSONArray("instructions").forEach {instruction ->
            instructionSet += "https://static.mvideo.ru/$instruction"
        }
        product.instructions = instructionSet.toSet()

//        val propertiesGroupSet: MutableSet<PropertiesGroup> = mutableSetOf()
//        ((objectJSON.get("properties") as JSONObject).get("all") as JSONArray).forEach {group ->
//            val propertiesSet: MutableSet<Properties> = mutableSetOf()
//            val propertiesGroupName: String = (group as JSONObject).get("name").toString()
//            (group.get("properties") as JSONArray).forEach { properties ->
//                val propertiesId: Long = (properties as JSONObject).get("id").toString().toLong()
//                if (propertiesRepository.findById(propertiesId).isEmpty) {
//                    propertiesRepository.save(
//                        Properties(
//                            propertiesId,
//                            properties.get("name").toString(),
//                            properties.get("value").toString(),
//                            properties.get("nameDescription").toString(),
//                        )
//                    )
//                }
//                propertiesSet += propertiesRepository.findById(propertiesId).get()
//            }
//
//            propertiesGroupSet += PropertiesGroup(
//                propertiesGroupName,
//                propertiesSet.toSet()
//            )
//        }
//        product.properties = propertiesGroupSet.toSet()

        product.url = "https://www.mvideo.ru/products/${product.nameTranslit}-${product.id}"
        productRepository.save(product)
        sleep()
    }
}
