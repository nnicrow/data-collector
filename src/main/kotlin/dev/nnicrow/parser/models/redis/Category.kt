package dev.nnicrow.parser.models.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("Category")
open class Category(
    @Id val id: Long,
    var name: String,
    var innerId: Long
)