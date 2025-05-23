package com.practice.autocare.api

import com.practice.autocare.models.auth.LoginRequest
import com.practice.autocare.models.auth.RegisterAutoRequest
import com.practice.autocare.models.auth.RegisterAutoResponse
import com.practice.autocare.models.auth.RegisterUserRequest
import com.practice.autocare.models.auth.RegisterUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {
    //POST http://localhost:8080/auth/register
    //Content-Type: application/json
    //{
    //    "full_name": "Joe Hugo Kennedy",
    //    "email": "hugo@gamil.com",
    //    "password": "Joe"
    //}
    @POST("auth/register")
    suspend fun registerUser(
        @Body registerUserRequest: RegisterUserRequest
    ): Response<RegisterUserResponse>

//    POST http://localhost:8000/cars/
//    Content-Type: application/json
//    email: alice@example.com
//    {
//        "brand": "Toyota",
//        "model": "Corolla",
//        "year": 2020,
//        "mileage": 45000
//    }
    @POST("/cars")
    suspend fun registerAuto(
    @Header("email") email: String,
    @Body registerAutoRequest: RegisterAutoRequest
): Response<RegisterAutoResponse>

//    POST http://localhost:8080/auth/login
//    Content-Type: application/json
//    {
//        "email": "pierre@example.com",
//        "password": "1234"
//    }
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<RegisterUserResponse>
}