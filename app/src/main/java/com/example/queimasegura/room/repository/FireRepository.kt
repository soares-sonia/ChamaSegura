package com.example.queimasegura.room.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.queimasegura.R
import com.example.queimasegura.room.dao.FireDao
import com.example.queimasegura.room.entities.Fire


class FireRepository(private val fireDao: FireDao) {
    val readData: LiveData<List<Fire>> = fireDao.readFiresData()

    suspend fun addFire(fire: Fire) {
        fireDao.addFire(fire)
    }

    suspend fun clearFires() {
        fireDao.clearFires()
    }

    suspend fun removeFire(fireId: String) {
        fireDao.removeFire(fireId)
    }

    suspend fun getNextFire(application: Application): Fire? {
        val statuses = listOf((application.getString(R.string.fire_status_scheduled)), application.getString(R.string.fire_status_ongoing))
        return fireDao.nextFire(statuses)
    }
}