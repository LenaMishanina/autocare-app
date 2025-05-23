package com.practice.autocare.models.auth

data class RegisterAutoRequest(
    val brand: String,
    val model: String,
    val year: Short,
    val mileage: Int,
)
