package com.example.queimasegura.user.fragments.fire

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.entities.Fire
import com.example.queimasegura.room.repository.AuthRepository
import com.example.queimasegura.room.repository.FireRepository

class FiresViewModel(
    private val application: Application,
    private val retrofitRepository: Repository
): ViewModel()  {
    val authData: LiveData<Auth>
    val firesData: LiveData<List<Fire>>

    private val authRepository: AuthRepository
    private val fireRepository: FireRepository

    init {
        val database = AppDataBase.getDatabase(application)
        authRepository = AuthRepository(database.authDao())
        fireRepository = FireRepository(database.fireDao())

        authData = authRepository.readData
        firesData = fireRepository.readData
    }
}