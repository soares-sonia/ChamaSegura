package com.example.queimasegura.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "location_table")
data class ZipCode(
    @PrimaryKey
    val id: Int,
    val locationName: String,
    val zipCode: String,
    val artName: String?,
    val tronco: String?
)
