package com.example.queimasegura.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.queimasegura.room.entities.Type


@Dao
interface TypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addType(type: Type)

    @Query("DELETE FROM types_table")
    suspend fun clearTypes()

    @Query("SELECT * FROM types_table ORDER BY id")
    fun readTypesData(): LiveData<List<Type>>
}