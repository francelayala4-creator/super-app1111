package com.superapp.ui.screens.quote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superapp.data.api.dto.QuoteRequestDto
import com.superapp.data.api.dto.QuoteResponseDto
import com.superapp.data.repository.QuoteRepository
import com.superapp.util.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuoteUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val quoteResponse: QuoteResponseDto? = null,
)

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val repo: QuoteRepository,
    private val location: LocationProvider,
) : ViewModel() {
    private val _state = MutableStateFlow(QuoteUiState())
    val state = _state.asStateFlow()

    fun run(listId: String, strategy: String = "best_balance") = viewModelScope.launch {
        _state.value = QuoteUiState(loading = true)
        val loc = runCatching { location.lastKnown() }.getOrNull()
        runCatching {
            repo.quote(QuoteRequestDto(
                shopping_list_id = listId,
                user_lat = loc?.latitude ?: 20.5888,
                user_lng = loc?.longitude ?: -100.3899,
                strategy = strategy,
            ))
        }
            .onSuccess { _state.value = QuoteUiState(quoteResponse = it) }
            .onFailure { _state.value = QuoteUiState(error = it.message ?: "Error de cotización") }
    }

    /** Re-cargar resultados por id de cotización (usado por ResultsScreen). */
    fun loadById(quoteId: String) = viewModelScope.launch {
        if (_state.value.quoteResponse?.quote_id == quoteId) return@launch
        _state.value = QuoteUiState(loading = true)
        runCatching { repo.get(quoteId) }
            .onSuccess { _state.value = QuoteUiState(quoteResponse = it) }
            .onFailure { _state.value = QuoteUiState(error = it.message ?: "Error cargando resultados") }
    }
}
