package com.example.smartlab.model.api

import com.example.smartlab.model.api.callModels.ProfileRequest
import com.example.smartlab.model.api.responseModels.ProfileResponse
import com.example.smartlab.model.api.responseModels.TokenResponse
import com.example.smartlab.model.dto.CatalogItem
import com.example.smartlab.model.dto.NewsItem
import com.example.smartlab.model.dto.Order
import com.example.smartlab.model.dto.OrderId
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface SmartLabService {

    @POST("api/sendCode")
    suspend fun sendCode(@Header("email") email: String): Response<Unit>

    @POST("api/signin")
    suspend fun signIn(
        @Header("email") email: String,
        @Header("code") code: String,
    ): Response<TokenResponse>

    @POST("api/createProfile")
    suspend fun createProfile(
        @Header("Authorization") token: String,
        @Body userBody: ProfileRequest,
    ): Response<ProfileResponse>

    @PUT("api/updateProfile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body profile: ProfileRequest,
    ): Response<ProfileResponse>

    @GET("api/news")
    suspend fun getNews(): Response<List<NewsItem>>

    @GET("api/catalog")
    suspend fun getCatalog(): Response<List<CatalogItem>>

    @Multipart
    @POST("api/avatar")
    suspend fun updateAvatar(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<Unit>

    @POST("api/order")
    suspend fun order(@Body order: Order): Response<OrderId>
}