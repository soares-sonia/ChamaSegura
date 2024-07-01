package com.example.queimasegura.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "status_table")
data class Status(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val firesComplete: Int,
    val firesPending: Int
)
