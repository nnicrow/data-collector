package dev.nnicrow.parser.repository

import dev.nnicrow.parser.models.redis.UploadedData
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UploadedDataRepository : CrudRepository<UploadedData?, Long?>