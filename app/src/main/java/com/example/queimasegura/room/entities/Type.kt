package com.example.queimasegura.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "types_table")
data class Type(
    @PrimaryKey
    val id: Int,
    val namePt: String,
    val nameEn: String
)
