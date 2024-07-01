package com.example.queimasegura.common

import java.util.UUID

data class Pedido(
    val type: String,
    val date: String,
    val state: String,
    val id: UUID = UUID.randomUUID()
)