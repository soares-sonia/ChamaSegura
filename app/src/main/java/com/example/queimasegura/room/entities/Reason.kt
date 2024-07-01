package com.example.queimasegura.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "reasons_table")
data class Reason(
    @PrimaryKey
    val id: Int,
    val namePt: String,
    val nameEn: String
)
