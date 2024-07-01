package com.example.queimasegura.retrofit.model.admin.get

import com.example.queimasegura.retrofit.model.admin.data.AdminStatus


data class AdminStatus(
    val status: String,
    val message: String,
    val result: AdminStatus
)
