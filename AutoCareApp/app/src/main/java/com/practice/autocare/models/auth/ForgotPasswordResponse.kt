package com.practice.autocare.models.auth

data class ForgotPasswordResponse(
    val message: String,
    val reset_token: String
)
