package com.example.queimasegura.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.queimasegura.room.entities.Auth


@Dao
interface AuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun auth(auth: Auth)

    @Query("DELETE FROM auth_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM auth_table LIMIT 1")
    fun readAuthData(): LiveData<Auth>
}