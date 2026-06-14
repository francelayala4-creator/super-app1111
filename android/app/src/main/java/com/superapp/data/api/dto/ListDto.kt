package com.superapp.data.api.dto
import kotlinx.serialization.Serializable

@Serializable data class ListItemDto(
    val id: String? = null,
    val raw_name: String,
    val product_id: String? = null,
    val quantity: Double = 1.0,
    val unit: String? = null,
)

@Serializable data class CreateListDto(val name: String, val notes: String? = null, val items: List<ListItemDto> = emptyList())
@Serializable data class ShoppingListDto(val id: String, val name: String, val notes: String? = null, val items: List<ListItemDto> = emptyList())
