package com.example.queimasegura.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.queimasegura.room.entities.Controller


@Dao
interface ControllerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addController(type: Controller)

    @Query("DELETE FROM controller_table")
    suspend fun clearController()

    @Query("SELECT * FROM controller_table LIMIT 1")
    suspend fun getController(): Controller?
}