package com.practice.autocare.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val interceptor = HttpLoggingInterceptor()
    //TODO interceptor.level = HttpLoggingInterceptor.Level.BODY

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://127.0.0.1:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val mainApi = retrofit.create(AuthApi::class.java)
}