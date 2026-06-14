package com.superapp.data.api.dto
import kotlinx.serialization.Serializable

@Serializable data class ChainDto(val id: String, val slug: String, val name: String, val color: String? = null, val logo_url: String? = null)
@Serializable data class StoreDto(val id: String, val chain_id: String, val name: String, val address: String? = null,
                                  val latitude: Double, val longitude: Double, val distance_km: Double? = null)
