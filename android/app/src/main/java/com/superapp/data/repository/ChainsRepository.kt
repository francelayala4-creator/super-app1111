package com.superapp.data.repository

import com.superapp.data.api.SuperApi
import javax.inject.Inject

class ChainsRepository @Inject constructor(private val api: SuperApi) {
    suspend fun chains() = api.chains()
    suspend fun nearby(lat: Double, lng: Double) = api.nearby(lat, lng)
}
