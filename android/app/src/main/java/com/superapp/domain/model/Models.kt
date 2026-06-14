package com.superapp.domain.model

import com.superapp.data.api.dto.*

data class QuotedChain(
    val resultId: String,
    val chainId: String,
    val chainName: String,
    val storeName: String?,
    val total: Double,
    val itemsFound: Int,
    val itemsMissing: Int,
    val distanceKm: Double?,
    val etaMinutes: Double?,
    val score: Double,
    val rank: Int,
    val savings: Double?,
    val items: List<QuoteItemDto>,
)

fun QuoteResultDto.toDomain() = QuotedChain(
    resultId = id, chainId = chain_id, chainName = chain_name, storeName = store_name,
    total = total, itemsFound = items_found, itemsMissing = items_missing,
    distanceKm = distance_km, etaMinutes = eta_minutes, score = score,
    rank = rank, savings = savings_vs_max, items = items,
)
