package dev.nnicrow.parser.repository

import dev.nnicrow.parser.models.redis.Brand
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BrandRepository : CrudRepository<Brand?, Long?>