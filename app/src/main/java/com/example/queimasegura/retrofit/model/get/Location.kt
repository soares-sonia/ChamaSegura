package com.example.queimasegura.retrofit.model.get

import com.example.queimasegura.retrofit.model.data.Location


data class Location(
    val status: String,
    val message: String,
    val result: List<Location>,
)
