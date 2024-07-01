package com.example.queimasegura.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.queimasegura.room.entities.Fire


@Dao
interface FireDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFire(fire: Fire)

    @Query("DELETE FROM fire_table")
    suspend fun clearFires()

    @Query("DELETE FROM fire_table WHERE id = :fireId")
    suspend fun removeFire(fireId: String)

    @Query("SELECT * FROM fire_table ORDER BY date ASC")
    fun readFiresData(): LiveData<List<Fire>>

    @Query("SELECT * FROM fire_table WHERE status IN (:statuses) ORDER BY date ASC LIMIT 1")
    suspend fun nextFire(statuses: List<String>): Fire?
}