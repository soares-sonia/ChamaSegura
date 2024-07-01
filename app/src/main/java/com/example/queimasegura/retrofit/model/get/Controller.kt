package com.example.queimasegura.retrofit.model.get

import com.example.queimasegura.retrofit.model.data.Controller


data class Controller(
    val status: String,
    val message: String,
    val result: Controller,
)
