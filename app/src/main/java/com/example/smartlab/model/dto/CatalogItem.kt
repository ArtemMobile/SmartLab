package com.example.smartlab.model.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analyzes")
data class CatalogItem(
    @PrimaryKey
    val id: Int,
    val bio: String,
    val category: String,
    val description: String,
    val name: String,
    val preparation: String,
    val price: String,
    val time_result: String,
    var isInCard: Boolean = false,
    var totalPrice: Int = 0,
    var count: Int = 0,
    var patientCount: Int = 1
)