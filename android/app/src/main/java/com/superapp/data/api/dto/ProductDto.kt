package com.superapp.data.api.dto
import kotlinx.serialization.Serializable

@Serializable data class ProductDto(
    val id: String,
    val name: String,
    val brand: String? = null,
    val category: String? = null,
    val presentation: String? = null,
    val size: Double? = null,
    val unit: String? = null,
    val image_url: String? = null,
)
@Serializable data class SearchResponseDto(val products: List<ProductDto>, val total: Int)
