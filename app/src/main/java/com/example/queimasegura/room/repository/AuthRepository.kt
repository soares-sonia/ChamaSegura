package com.example.queimasegura.room.repository

import androidx.lifecycle.LiveData
import com.example.queimasegura.room.dao.AuthDao
import com.example.queimasegura.room.entities.Auth

class AuthRepository(private val authDao: AuthDao) {
    val readData: LiveData<Auth> = authDao.readAuthData()

    suspend fun authenticate(auth: Auth){
        authDao.auth(auth)
    }

    suspend fun delAuth(){
        authDao.deleteAll()
    }
}