package com.example.queimasegura.retrofit.model.get

import com.example.queimasegura.retrofit.model.data.FireDetails


data class FireDetails(
    val status: String,
    val message: String,
    val result: FireDetails
)
