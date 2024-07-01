package com.example.queimasegura.retrofit.model.data

import com.google.gson.annotations.SerializedName


data class Location(
    val id: Int,
    @SerializedName("location_name")
    val locationName: String,
    @SerializedName("zip_code")
    val zipCode: String,
    @SerializedName("ART_name")
    val artName: String?,
    val tronco: String?
)
