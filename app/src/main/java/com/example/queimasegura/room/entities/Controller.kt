package com.example.queimasegura.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "controller_table")
data class Controller(
    @PrimaryKey
    val id: Int,
    val name: String
)
