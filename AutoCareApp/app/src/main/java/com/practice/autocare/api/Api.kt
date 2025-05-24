package com.practice.autocare.api

import com.practice.autocare.models.auth.LoginRequest
import com.practice.autocare.models.auth.RegisterAutoRequest
import com.practice.autocare.models.auth.RegisterAutoResponse
import com.practice.autocare.models.auth.RegisterUserRequest
import com.practice.autocare.models.auth.RegisterUserResponse
import com.practice.autocare.models.service.CarResponse
import com.practice.autocare.models.service.DeleteResponse
import com.practice.autocare.models.service.ServiceEventResponse
import com.practice.autocare.models.service.ServiceEventResponseComp
import com.practice.autocare.models.service.ServiceRequest
import com.practice.autocare.models.service.UpdateServiceStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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


//    GET http://192.168.1.84:8080/services/
//    Content-Type: application/json
//    email: wars@a.a
//    [
//    {
//        "car_id": 1,
//        "service_type": "Light change",
//        "due_date": "2025-04-20",
//        "due_mileage": 400000.0,
//        "event_id": 1
//    },
//          ...
//    {
//        "car_id": 1,
//        "service_type": "boba fett",
//        "due_date": "2023-05-25",
//        "due_mileage": 23789.0,
//        "event_id": 4
//    }
//    ]
    @GET("services/")
    suspend fun getServiceEvents(
        @Header("email") email: String
    ): Response<ArrayList<ServiceEventResponse>>


//    POST http://localhost:8080/services/
//    Content-Type: application/json
//    email: dorzeaizzy@gmail.com
//    {
//        "car_id": 12,
//        "service_type": "Bumper change",
//        "due_date": "2025-04-20",
//        "due_mileage": 190000
//    }
    @POST("services/")
    suspend fun addServiceEvent(
        @Header("email") email: String,
        @Body serviceRequest: ServiceRequest
    ): Response<ServiceEventResponse>

//    DELETE http://localhost:8080/services/5
//    Content-Type: application/json
//    email: dorzeaizzy@gmail.com
//    response
//    {
//        "message": "Service event deleted"
//    }
    @DELETE("services/{event_id}")
    suspend fun deleteServiceEvents(
        @Header("email") email: String,
        @Path("event_id") eventId: Int
    ): Response<DeleteResponse>


//    GET http://localhost:8080/cars
//    Content-Type: application/json
//    email: dorzeaizzy@gmail.com
//    response
//    [
//    {
//        "brand": "Peugeot",
//        "model": "208",
//        "year": 2020,
//        "mileage": 34000,
//        "car_id": 8,
//        "user_id": 8
//    }
//]
    @GET("cars/")
    suspend fun getCars(
        @Header("email") email: String
    ): Response<ArrayList<CarResponse>>


    // GET http://192.168.1.84:8080/services/?due_date=2025-05-30&is_completed=false
    // Content-Type: application/json
    // email: dorzeaizzy@gmail.com
    @GET("services/")
    suspend fun getServiceEvents(
        @Header("email") email: String,
        @Query("due_date") dueDate: String? = null,
        @Query("is_completed") isCompleted: Boolean? = null
    ): Response<ArrayList<ServiceEventResponseComp>>

    // PUT http://192.168.1.84:8080/services/{event_id}
    // Content-Type: application/json
    // email: dorzeaizzy@gmail.com
    // body: {"is_completed": true}
    @PUT("services/{event_id}")
    suspend fun updateServiceEventStatus(
        @Header("email") email: String,
        @Path("event_id") eventId: Int,
        @Body updateRequest: UpdateServiceStatusRequest
    ): Response<ServiceEventResponseComp>

}