package com.example.smartlab.model.dto

data class Address(
    val house_number: String,
    val road: String,
    val suburb: String,
    val city_district: String,
    val city: String,
    val county: String,
    val state: String,
    val `ISO3166-2-lvl4`: String,
    val region: String,
    val postcode: String,
    val country: String,
    val country_code: String,
)
