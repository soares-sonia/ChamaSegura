package com.example.queimasegura.retrofit.model.get

import com.example.queimasegura.retrofit.model.data.Reason


data class Reasons(
    val status: String,
    val message: String,
    val result: List<Reason>,
)
