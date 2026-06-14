package com.superapp.data.api.dto
import kotlinx.serialization.Serializable

@Serializable data class FavoriteDto(val id: String, val product_id: String)
@Serializable data class FavoriteRequestDto(val product_id: String)

@Serializable data class AlertDto(val id: String, val product_id: String, val target_price: Double, val type: String, val is_active: Boolean)
@Serializable data class AlertRequestDto(val product_id: String, val target_price: Double, val type: String = "price_drop")

@Serializable data class HistoryDto(
    val id: String,
    val strategy: String,
    val created_at: String,
    val summary: Map<String, kotlinx.serialization.json.JsonElement>? = null,
    val results_count: Int,
)
