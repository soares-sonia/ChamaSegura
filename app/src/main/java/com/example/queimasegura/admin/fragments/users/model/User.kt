package com.example.queimasegura.admin.fragments.users.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    var type: Int,
    var active: Boolean,
    var deleted: Boolean
)
