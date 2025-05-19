package com.practice.autocare.models.auth

data class RegisterUserResponse(
    val access_token: String,
    val token_type: String,
)
