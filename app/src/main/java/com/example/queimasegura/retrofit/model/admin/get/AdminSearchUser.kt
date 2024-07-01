package com.example.queimasegura.retrofit.model.admin.get

import com.example.queimasegura.retrofit.model.admin.data.AdminSearchUser


data class AdminSearchUser(
    val status: String,
    val message: String,
    val result: List<AdminSearchUser>
)
