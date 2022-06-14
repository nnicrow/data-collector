package dev.nnicrow.parser.repository

import dev.nnicrow.parser.models.redis.Properties
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PropertiesRepository : CrudRepository<Properties?, Long?>