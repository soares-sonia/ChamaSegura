package com.example.queimasegura.retrofit.model.admin.get

import com.example.queimasegura.retrofit.model.admin.data.AdminGetUsers


data class AdminGetUsers(
    val status: String,
    val message: String,
    val result: List<AdminGetUsers>
)