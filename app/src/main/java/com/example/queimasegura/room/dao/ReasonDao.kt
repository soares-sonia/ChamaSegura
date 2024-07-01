package com.example.queimasegura.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.queimasegura.room.entities.Reason


@Dao
interface ReasonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReason(type: Reason)

    @Query("DELETE FROM reasons_table")
    suspend fun clearReasons()

    @Query("SELECT * FROM reasons_table ORDER BY id")
    fun readReasonsData(): LiveData<List<Reason>>
}