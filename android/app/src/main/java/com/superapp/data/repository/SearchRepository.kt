package com.superapp.data.repository

import com.superapp.data.api.SuperApi
import javax.inject.Inject

class SearchRepository @Inject constructor(private val api: SuperApi) {
    suspend fun search(q: String) = api.search(q)
}
