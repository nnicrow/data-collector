package dev.nnicrow.parser.services

import dev.nnicrow.parser.models.redis.Product
import dev.nnicrow.parser.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class DataUploader(
    private val mainServerRequest: MainServerRequest
) {
    fun productUploader(productRepository: ProductRepository, webShopId: Int) {
        val products = productRepository.findAll()
        val productsJSON: MutableList<String?> = mutableListOf()
        products.forEach { product: Product? ->
            product?.let {
                if ((product.name.isNullOrBlank() || product.modelName.isNullOrEmpty() || product.nameTranslit.isNullOrEmpty()) || (product.innerId != null && product.occupancy >= 60 )) return@forEach

                val imagesJSON: MutableList<String> = mutableListOf()
                it.images?.forEach { image ->
                    imagesJSON.add("{ url: \\\"${image}\\\"}")
                }
                productsJSON.add ("{ " +
                        "id: \\\"${product.id}\\\" " +
                        "name: \\\"${product.name}\\\" " +
                        "brand: { " +
                        "id: \\\"${product.brand?.id}\\\" " +
                        "brandName: \\\"${product.brand?.brandName}\\\" " +
                        "brandNameTranslit: \\\"${product.brand?.brandNameTranslit}\\\" " +
                        "brandUrl: \\\"${product.brand?.brandUrl}\\\" " +
                        "brandLogoUrl: \\\"${product.brand?.brandLogoUrl}\\\" " +
                        "isBrandVisible: ${product.brand?.isBrandVisible} " +
                        "innerId: ${product.brand?.innerId} " +
                        "} " +
                        "category: { " +
                        "id: \\\"${product.category?.id}\\\" " +
                        "name: \\\"${product.category?.name}\\\" " +
                        "innerId: ${product.category?.innerId} " +
                        "} " +
                        "images: ${imagesJSON.toString()} " +
                        "nameTranslit: \\\"${product.nameTranslit}\\\" " +
                        "description: \\\"${product.description}\\\" " +
                        "modelName: \\\"${product.modelName}\\\" " +
                        "url: \\\"${product.url}\\\" " +
                        "innerId: ${product.innerId} }")
            }
        }
        val jsonInputString: String = "{\"operationName\":null,\"variables\":{},\"query\":\"mutation {" +
                " addProducts(input: {webShop: $webShopId products: ${productsJSON.toString()}}) { status error } }\"}"
        mainServerRequest.sendDataServer(jsonInputString)
    }
}