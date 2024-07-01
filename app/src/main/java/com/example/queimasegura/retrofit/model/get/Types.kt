package com.example.queimasegura.retrofit.model.get

import com.example.queimasegura.retrofit.model.data.Type


data class Types(
    val status: String,
    val message: String,
    val result: List<Type>,
)
