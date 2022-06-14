package dev.nnicrow.parser.services

import dev.nnicrow.parser.models.redis.Product
import dev.nnicrow.parser.repository.PriceRepository
import dev.nnicrow.parser.repository.ProductRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.logging.Logger


@Component
class TaskManager (
    private val mainServerRequest: MainServerRequest,
    private val dataSynchronizer: DataSynchronizer,
    private val parseManager: ParseManager,
    private val productRepository: ProductRepository,
    private val priceRepository: PriceRepository,
    private val dataUploader: DataUploader
    ) {
    private val log: Logger = Logger.getLogger(TaskManager::class.java.name)

    @Scheduled(fixedDelay = 10800000)
    fun parseMain() {
        /**
         * Основная функция планироващик приложения. При инициализации обращается к центральнму сервису и получает с него данные.
         * После синхронизирует их с локальной БД. Далее происходит выгрузка, после которой начинается сбор данных с каждой категории,
         * а после с каждого продукта и производится выгрузка
         *
         * @author nnicrow
         */
        val shopName: String = "М.Видео"

        val webShopId = mainServerRequest.getDataFromServer("М.Видео") ?: return

        dataSynchronizer.categorySynchronize(mainServerRequest.categoryList)

        dataUploader.productUploader(productRepository, webShopId)
        mainServerRequest.categoryList.forEach {category ->
            parseManager.categoryParse(category.externalId)

        }

        productRepository.findAll().forEach {product: Product? ->
            if ((product?.occupancy ?: 0) > 60) return@forEach
            product?.let { parseManager.productParse(it) }
        }
        log.info("End data synchronize and parse")

        dataUploader.productUploader(productRepository, webShopId)
    }

//    @Scheduled(fixedDelay = 300000L)
    fun parsePriceAndUpload(): Unit {
    /**
     * Функция сбора цен с серверов, и выгразки их на центральный сервер
     *
     * @author nnicrow
     */
//        val products: List<List<Product?>> = productRepository.findAll().chunked(24)
//        products.forEach { productsSet: List<Product?> ->
//            parseManager.parsePrice(productsSet)
//        }
//        log.info("End price parse")
//        val price = JSONArray(priceRepository.findAll())
//        println()
    }
}