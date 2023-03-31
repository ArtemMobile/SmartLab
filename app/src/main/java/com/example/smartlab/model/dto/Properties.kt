package com.example.smartlab.model.dto

data class Properties(
    val place_id: Int,
    val osm_type: String,
    val osm_id: Int,
    val place_rank: Int,
    val category: String,
    val type: String,
    val importance: String,
    val addresstype: String,
    val name: String?,
    val display_name: String,
    val address: Address,
    val bbox: List<Float>,
    val geometry: Geometry
)