package com.practice.autocare.models.auth

data class RegisterUserRequest(
    val full_name: String,
    val email: String,
    val password: String,
)