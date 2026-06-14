package com.superapp.data.repository

import com.superapp.data.api.SuperApi
import com.superapp.data.api.dto.LoginDto
import com.superapp.data.api.dto.RegisterDto
import com.superapp.data.local.datastore.AuthStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: SuperApi,
    private val store: AuthStore,
) {
    suspend fun register(email: String, password: String, name: String?) {
        val t = api.register(RegisterDto(email, password, name))
        store.save(t.access_token, t.refresh_token)
    }
    suspend fun login(email: String, password: String) {
        val t = api.login(LoginDto(email, password))
        store.save(t.access_token, t.refresh_token)
    }
    suspend fun logout() = store.clear()
    suspend fun isLoggedIn() = store.isLoggedIn()
}
