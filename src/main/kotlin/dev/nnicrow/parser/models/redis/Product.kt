package dev.nnicrow.parser.models.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("Product")
open class Product(
    @Id val id: Long,
    var name: String? = null,
    var brand: Brand? = null,
    var nameTranslit: String? = null,
    var images: Set<String>? = null,
    var category: Category? = null,
    var description: String? = null,
    var modelName: String? = null,
    var properties: Set<PropertiesGroup>? = null,
    var certificates: Set<String>? = null,
    var instructions: Set<String>? = null,
    var url: String? = null,
    var innerId: Long? = null,
    var occupancy: Int = 0
)