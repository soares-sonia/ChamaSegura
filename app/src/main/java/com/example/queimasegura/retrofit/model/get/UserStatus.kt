package com.example.queimasegura.retrofit.model.get

import com.example.queimasegura.retrofit.model.data.UserStatus


data class UserStatus(
    val status: String,
    val message: String,
    val result: UserStatus
)
