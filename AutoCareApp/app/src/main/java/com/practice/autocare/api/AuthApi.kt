package com.practice.autocare.api

import com.practice.autocare.models.auth.RegisterUserRequest
import com.practice.autocare.models.auth.RegisterUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

//POST http://localhost:8080/auth/register
//Content-Type: application/json
//
//{
//    "full_name": "Joe Hugo Kennedy",
//    "email": "hugo@gamil.com",
//    "password": "Joe"
//}

interface AuthApi {
    @POST("auth/register")
    suspend fun registerUser(
        @Body registerUserRequest: RegisterUserRequest
    ): Response<RegisterUserResponse>
}