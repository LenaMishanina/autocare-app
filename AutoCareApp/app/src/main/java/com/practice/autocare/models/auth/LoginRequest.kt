package com.practice.autocare.models.auth

data class LoginRequest(
    val email: String,
    val password: String,
)
