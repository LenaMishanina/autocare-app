package com.practice.autocare.models.auth

data class ResetPasswordRequest(
    val token: String,
    val new_password: String,
)
