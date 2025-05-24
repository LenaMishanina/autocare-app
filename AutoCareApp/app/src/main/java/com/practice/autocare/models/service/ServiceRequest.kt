package com.practice.autocare.models.service

data class ServiceRequest(
    val car_id: Int,
    val service_type: String,
    val due_date: String,  // Формат "YYYY-MM-DD"
    val due_mileage: Double,
)
