package dev.nnicrow.parser.repository

import dev.nnicrow.parser.models.redis.Category
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : CrudRepository<Category?, Long?>