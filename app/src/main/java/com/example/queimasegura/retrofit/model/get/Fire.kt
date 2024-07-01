package com.example.queimasegura.retrofit.model.get

import com.example.queimasegura.retrofit.model.data.Fire


data class Fire(
    val status: String,
    val message: String,
    val result: List<Fire>
)
