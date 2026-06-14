package com.superapp.data.api.dto
import kotlinx.serialization.Serializable

@Serializable data class RunScrapingDto(val chain_slug: String? = null)
@Serializable data class ScrapingJobDto(
    val id: String,
    val chain_id: String? = null,
    val status: String,
    val started_at: String? = null,
    val finished_at: String? = null,
    val items_scraped: Int,
    val items_failed: Int,
    val triggered_by: String,
    val error: String? = null,
)
