package dev.nnicrow.parser.models.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("PriceAndAvailability")
open class Price (
    @Id var productId: Long,
    var basePrice: Int? = null,
    var salePrice: Int? = null,
)