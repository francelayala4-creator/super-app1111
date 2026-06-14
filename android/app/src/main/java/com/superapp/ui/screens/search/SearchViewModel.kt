package com.superapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superapp.data.api.dto.ProductDto
import com.superapp.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(val results: List<ProductDto> = emptyList(), val loading: Boolean = false, val error: String? = null)

@HiltViewModel
class SearchViewModel @Inject constructor(private val repo: SearchRepository) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()
    private var job: Job? = null

    fun search(q: String) {
        job?.cancel()
        if (q.length < 2) { _state.value = SearchState(); return }
        job = viewModelScope.launch {
            delay(220)
            _state.value = _state.value.copy(loading = true)
            runCatching { repo.search(q) }
                .onSuccess { _state.value = SearchState(results = it.products) }
                .onFailure { _state.value = SearchState(error = it.message) }
        }
    }
}
