package com.superapp.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val auth: AuthRepository) : ViewModel() {
    private val _isLogged = MutableStateFlow<Boolean?>(null)
    val isLogged: StateFlow<Boolean?> = _isLogged
    fun checkAuth() { viewModelScope.launch { _isLogged.value = auth.isLoggedIn() } }
}
