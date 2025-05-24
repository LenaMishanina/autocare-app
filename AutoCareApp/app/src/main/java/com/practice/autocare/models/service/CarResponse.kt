package com.practice.autocare.models.service

data class CarResponse(
    val brand: String,
    val model: String,
    val year: Short,
    val mileage: Int,
    val car_id: Int,
    val user_id: Int,
)
