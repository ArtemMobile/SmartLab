package com.example.smartlab.model.api.callModels

data class ProfileRequest(
    val bith: String,
    val firstname: String,
    val image: String,
    val lastname: String,
    val middlename: String,
    val pol: String
)