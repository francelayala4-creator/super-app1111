package com.superapp.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superapp.data.api.dto.ListItemDto
import com.superapp.data.api.dto.ShoppingListDto
import com.superapp.data.repository.ListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateState(val loading: Boolean = false, val error: String? = null)
data class EditorState(val list: ShoppingListDto? = null, val loading: Boolean = false, val error: String? = null)

@HiltViewModel
class ListsViewModel @Inject constructor(private val repo: ListRepository) : ViewModel() {
    private val _create = MutableStateFlow(CreateState())
    val create = _create.asStateFlow()

    private val _editor = MutableStateFlow(EditorState())
    val editor = _editor.asStateFlow()

    fun createNew(name: String, onOk: (String) -> Unit) = viewModelScope.launch {
        _create.value = CreateState(loading = true)
        runCatching { repo.create(name, emptyList()) }
            .onSuccess { _create.value = CreateState(); onOk(it.id) }
            .onFailure { _create.value = CreateState(error = it.message) }
    }

    fun loadEditor(id: String) = viewModelScope.launch {
        _editor.value = EditorState(loading = true)
        runCatching { repo.get(id) }
            .onSuccess { _editor.value = EditorState(list = it) }
            .onFailure { _editor.value = EditorState(error = it.message) }
    }

    fun addItem(id: String, name: String, qty: Double = 1.0, productId: String? = null) = viewModelScope.launch {
        runCatching { repo.addItem(id, ListItemDto(raw_name = name, quantity = qty, product_id = productId)) }
        loadEditor(id)
    }

    fun removeItem(listId: String, itemId: String) = viewModelScope.launch {
        runCatching { repo.deleteItem(listId, itemId) }
        loadEditor(listId)
    }
}
