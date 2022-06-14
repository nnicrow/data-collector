package dev.nnicrow.parser.repository

import dev.nnicrow.parser.models.redis.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : CrudRepository<Product?, Long?>