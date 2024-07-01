package com.example.queimasegura.retrofit.model.send


data class CreateFireBody(
    val date: String,
    val typeId: Int,
    val reasonId: Int,
    val zipCodeId: Int,
    val location: String?,
    val observations: String?
)
