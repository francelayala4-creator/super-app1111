package com.superapp.ui.screens.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val loading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    fun setEmail(v: String) { _state.value = _state.value.copy(email = v.trim()) }
    fun setPassword(v: String) { _state.value = _state.value.copy(password = v) }
    fun setName(v: String) { _state.value = _state.value.copy(name = v) }

    fun login(onOk: () -> Unit) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        runCatching { repo.login(_state.value.email, _state.value.password) }
            .onSuccess { _state.value = _state.value.copy(loading = false); onOk() }
            .onFailure { _state.value = _state.value.copy(loading = false, error = it.message ?: "Error") }
    }

    fun register(onOk: () -> Unit) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        runCatching { repo.register(_state.value.email, _state.value.password, _state.value.name.ifBlank { null }) }
            .onSuccess { _state.value = _state.value.copy(loading = false); onOk() }
            .onFailure { _state.value = _state.value.copy(loading = false, error = it.message ?: "Error") }
    }
}
