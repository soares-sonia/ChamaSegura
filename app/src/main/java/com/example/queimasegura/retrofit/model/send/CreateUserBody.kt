package com.example.queimasegura.retrofit.model.send


data class CreateUserBody(
    val fullName: String,
    val email: String,
    var password: String,
    val nif: String
)
