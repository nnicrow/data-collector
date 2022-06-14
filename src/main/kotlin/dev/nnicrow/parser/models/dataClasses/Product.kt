package dev.nnicrow.parser.models.dataClasses

data class Product(
    val id: Long,
    val name: String,
    val externalId: Long,
    val nameTranslit: String,
    val description: String,
    val modelName: String,
    val url: String,
    val brand: Brand
)