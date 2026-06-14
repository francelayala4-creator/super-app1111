package com.superapp.data.api.dto
import kotlinx.serialization.Serializable

@Serializable data class QuoteRequestDto(
    val shopping_list_id: String,
    val user_lat: Double? = null,
    val user_lng: Double? = null,
    val strategy: String = "best_balance",
    val max_results: Int = 5,
)

@Serializable data class QuoteItemDto(
    val list_item_id: String,
    val raw_name: String,
    val matched: Boolean,
    val match_confidence: Double,
    val unit_price: Double? = null,
    val quantity: Double,
    val subtotal: Double? = null,
    val substitution_for: String? = null,
    val product_id: String? = null,
)

@Serializable data class QuoteResultDto(
    val id: String,
    val chain_id: String,
    val chain_name: String,
    val store_id: String? = null,
    val store_name: String? = null,
    val store_lat: Double? = null,
    val store_lng: Double? = null,
    val store_address: String? = null,
    val total: Double,
    val items_found: Int,
    val items_missing: Int,
    val distance_km: Double? = null,
    val eta_minutes: Double? = null,
    val score: Double,
    val rank: Int,
    val savings_vs_max: Double? = null,
    val items: List<QuoteItemDto> = emptyList(),
)

@Serializable data class QuoteResponseDto(
    val quote_id: String,
    val strategy: String,
    val results: List<QuoteResultDto>,
    val best_cheapest_id: String? = null,
    val best_nearest_id: String? = null,
    val best_balance_id: String? = null,
)
