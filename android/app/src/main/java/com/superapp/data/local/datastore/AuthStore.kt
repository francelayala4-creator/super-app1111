package com.superapp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "superapp_auth")

@Singleton
class AuthStore @Inject constructor(@ApplicationContext private val ctx: Context) {
    private val ACCESS = stringPreferencesKey("access")
    private val REFRESH = stringPreferencesKey("refresh")

    suspend fun save(access: String, refresh: String) {
        ctx.dataStore.edit { it[ACCESS] = access; it[REFRESH] = refresh }
    }
    suspend fun clear() { ctx.dataStore.edit { it.clear() } }
    suspend fun accessToken(): String? = ctx.dataStore.data.map { it[ACCESS] }.first()
    suspend fun refreshToken(): String? = ctx.dataStore.data.map { it[REFRESH] }.first()
    suspend fun isLoggedIn(): Boolean = !accessToken().isNullOrBlank()
}
