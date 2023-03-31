package com.example.smartlab.model.api

import com.example.smartlab.model.dto.ReverseGeocoding
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OsmService {

    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "geojson"
    ): Response<ReverseGeocoding>
}