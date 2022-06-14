package dev.nnicrow.parser.models.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("PropertiesGroup")
open class PropertiesGroup(
    @Id
    var name: String? = null,
    var properties: Set<Properties>? = null,
    var category: Category? = null
)