package dev.nnicrow.parser.models.dataClasses

data class Category(
    val id: Long,
    val name: String,
    val externalId: Long,
    val availability: Boolean,
    val product: MutableSet<Product>
)