package com.superapp.data.repository

import com.superapp.data.api.SuperApi
import com.superapp.data.api.dto.QuoteRequestDto
import com.superapp.data.api.dto.QuoteResponseDto
import javax.inject.Inject

class QuoteRepository @Inject constructor(private val api: SuperApi) {
    suspend fun quote(req: QuoteRequestDto): QuoteResponseDto = api.quote(req)
    suspend fun get(quoteId: String): QuoteResponseDto = api.getQuote(quoteId)
}
