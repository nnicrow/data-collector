package dev.nnicrow.parser.models.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("Brand")
open class Brand(
    @Id val id: Long,
    var brandName: String? = null,
    var brandNameTranslit: String? = null,
    var brandUrl: String? = null,
    var brandLogoUrl: String? = null,
    var isBrandVisible: Boolean? = null,
    var innerId: Long? = null
)