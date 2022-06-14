package dev.nnicrow.parser.models.redis

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("UploadedData")
open class UploadedData(
    @Id val id: Long
)