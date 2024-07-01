package com.example.queimasegura.retrofit.model.data

data class User(
    val id: String,
    val email: String,
    val password: String,
    val fullName: String,
    val nif: Int,
    val type: Int
)
