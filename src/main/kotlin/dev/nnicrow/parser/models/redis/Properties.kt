package dev.nnicrow.parser.models.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("Properties")
open class Properties(
    @Id val id: Long,
    var name: String? = null,
    var value: String? = null,
    var description: String? = null
)