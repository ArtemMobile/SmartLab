package com.example.smartlab.model.dto

data class ReverseGeocoding(
    val type: String,
    val licence: String,
    val features: List<Feature>
)