package com.practice.autocare.models.service

data class ServiceEventResponse(
    val car_id: Int,
    val service_type: String,
    val due_date: String,  // Формат "YYYY-MM-DD"
    val due_mileage: Double,
    val event_id: Int
)

data class ServiceEventResponseComp(
    val car_id: Int,
    val service_type: String,
    val due_date: String,  // Формат "YYYY-MM-DD"
    val due_mileage: Double,
    val event_id: Int,
    val is_completed: Boolean,
)
