package com.example.smartlab.model.dto

data class CatalogItem(
    val bio: String,
    val category: String,
    val description: String,
    val id: Int,
    val name: String,
    val preparation: String,
    val price: String,
    val time_result: String
)