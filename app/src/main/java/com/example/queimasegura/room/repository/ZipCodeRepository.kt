package com.example.queimasegura.room.repository

import androidx.lifecycle.LiveData
import com.example.queimasegura.room.dao.ZipCodeDao
import com.example.queimasegura.room.entities.ZipCode

class ZipCodeRepository(private val zipCodeDao: ZipCodeDao) {
    suspend fun getZips(): List<ZipCode>? {
        return zipCodeDao.getZipsData()
    }

    suspend fun addZipcode(zipCode: ZipCode) {
        if(!zipCodeDao.isZipCodeExists(zipCode.id)){
            zipCodeDao.addZip(zipCode)
        }
    }

    suspend fun clearZips() {
        zipCodeDao.clearZips()
    }
}