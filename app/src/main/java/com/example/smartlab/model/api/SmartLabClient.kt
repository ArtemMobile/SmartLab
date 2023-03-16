package com.example.smartlab.model.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SmartLabClient {
    private val interceptor =
        HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
    private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
    private val baseUrl = "https://medic.madskill.ru/"
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(SmartLabService::class.java)
}