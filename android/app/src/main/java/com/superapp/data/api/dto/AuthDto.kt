package com.superapp.data.api.dto
import kotlinx.serialization.Serializable

@Serializable data class RegisterDto(val email: String, val password: String, val full_name: String? = null)
@Serializable data class LoginDto(val email: String, val password: String)
@Serializable data class TokenPairDto(val access_token: String, val refresh_token: String, val token_type: String = "bearer")
@Serializable data class UserDto(val id: String, val email: String, val full_name: String? = null)
