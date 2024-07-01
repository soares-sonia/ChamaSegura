package com.example.queimasegura.retrofit.model.admin.data


data class AdminGetUsers(
    val userId: String,
    val fullName: String,
    val email: String,
    val type: Int,
    val active: Boolean,
    val deleted: Boolean
)
