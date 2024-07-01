package com.example.queimasegura.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.queimasegura.room.entities.ZipCode

@Dao
interface ZipCodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addZip(zipCode: ZipCode)

    @Query("DELETE FROM location_table")
    suspend fun clearZips()

    @Query("SELECT * FROM location_table LIMIT 25")
    suspend fun getZipsData(): List<ZipCode>?

    @Query("SELECT EXISTS(SELECT 1 FROM location_table WHERE id = :zipCodeId LIMIT 1)")
    suspend fun isZipCodeExists(zipCodeId: Int): Boolean
}