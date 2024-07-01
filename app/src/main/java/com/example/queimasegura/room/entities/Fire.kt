package com.example.queimasegura.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "fire_table")
data class Fire(
    @PrimaryKey
    val id: String,
    val date: String,
    val status: String,
    val type: String
)
