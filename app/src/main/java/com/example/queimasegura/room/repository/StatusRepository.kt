package com.example.queimasegura.room.repository

import androidx.lifecycle.LiveData
import com.example.queimasegura.room.dao.StatusDao
import com.example.queimasegura.room.entities.Status


class StatusRepository(private val statusDao: StatusDao) {
    val readData: LiveData<Status> = statusDao.readStatusData()

    suspend fun addStatus(status: Status) {
        statusDao.addStatus(status)
    }

    suspend fun addPending() {
        statusDao.addPending()
    }

    suspend fun addComplete() {
        statusDao.addComplete()
    }

    suspend fun clearStatus() {
        statusDao.clearStatus()
    }
}