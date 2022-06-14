package dev.nnicrow.parser.services

import dev.nnicrow.parser.models.dataClasses.Category
import dev.nnicrow.parser.models.redis.Brand
import dev.nnicrow.parser.models.redis.Product
import dev.nnicrow.parser.repository.BrandRepository
import dev.nnicrow.parser.repository.CategoryRepository
import dev.nnicrow.parser.repository.ProductRepository
import dev.nnicrow.parser.repository.PropertiesRepository
import org.springframework.stereotype.Service
import dev.nnicrow.parser.models.redis.Category as RedisCategory

@Service
class DataSynchronizer (
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
    private val categoryRepository: CategoryRepository,
    private val propertiesRepository: PropertiesRepository
    ) {

    fun categorySynchronize(categoryList: MutableSet<Category>) {
        categoryList.forEach { category: Category ->
            var redisCategory = categoryRepository.findById(category.externalId)
            if (redisCategory.isEmpty) {
                categoryRepository.save(
                    RedisCategory(
                        id = category.externalId,
                        name = category.name,
                        innerId = category.id
                    )
                )
            }

            redisCategory = categoryRepository.findById(category.externalId)
            category.product.forEach { product ->
                val redisProductObject = productRepository.findById(product.externalId)

                val redisBrandObject = brandRepository.findById(product.brand.externalId)
                val brand: Brand
                if (redisBrandObject.isEmpty) {
                    brand = Brand(
                        id = product.brand.externalId,
                        brandName = product.brand.name,
                        innerId = product.brand.id,
                    )
                    brandRepository.save(brand)
                }
                else brand = redisBrandObject.get()

                if (brand.innerId == null) {
                    brand.innerId = product.brand.id
                    brandRepository.save(brand)
                }

                val redisProduct: Product = if (redisProductObject.isEmpty) {
                    Product(
                        id = product.externalId,
                        name = product.name,
                        brand = brand,
                        nameTranslit = product.nameTranslit,
                        category = redisCategory.get(),
                        description = product.description,
                        modelName = product.modelName,
                        url = product.url,
                        innerId = product.id,
                    )
                } else redisProductObject.get()

                // Алгоритм оценки заполненности
                var occupancy: Int = 0
                if (redisProduct.name?.isNotEmpty() == true) occupancy += 30
                if (redisProduct.nameTranslit?.isNotEmpty() == true) occupancy += 30
                if (redisProduct.modelName?.isNotEmpty() == true) occupancy += 30
                redisProduct.innerId = product.id
                redisProduct.occupancy = occupancy

                productRepository.save(redisProduct)
            }
        }
    }
}