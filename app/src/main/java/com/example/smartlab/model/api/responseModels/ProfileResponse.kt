package com.example.smartlab.model.api.responseModels

data class ProfileResponse(
    val bith: String,
    val created_at: String,
    val firstname: String,
    val id: Int,
    val image: String,
    val lastname: String,
    val middlename: String,
    val pol: String,
    val updated_at: String,
    val user_id: Int
)