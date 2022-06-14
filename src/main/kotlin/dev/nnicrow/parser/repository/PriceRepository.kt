package dev.nnicrow.parser.repository

import dev.nnicrow.parser.models.redis.Price
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PriceRepository : CrudRepository<Price?, Long?>