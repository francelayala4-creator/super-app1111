package com.superapp.data.repository

import com.superapp.data.api.SuperApi
import javax.inject.Inject

class HistoryRepository @Inject constructor(private val api: SuperApi) {
    suspend fun history() = api.history()
    suspend fun favorites() = api.favorites()
    suspend fun alerts() = api.alerts()
}
