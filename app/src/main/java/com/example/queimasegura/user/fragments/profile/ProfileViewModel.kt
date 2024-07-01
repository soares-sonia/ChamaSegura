package com.example.queimasegura.user.fragments.profile

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.repository.AuthRepository
import com.example.queimasegura.room.repository.FireRepository
import com.example.queimasegura.util.ApiUtils
import com.example.queimasegura.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val application: Application,
    private val retrofitRepository: Repository
): ViewModel() {
    val authData: LiveData<Auth>

    private val authRepository: AuthRepository
    private val fireRepository: FireRepository

    init {
        val database = AppDataBase.getDatabase(application)
        authRepository = AuthRepository(database.authDao())
        authData = authRepository.readData
        fireRepository = FireRepository(database.fireDao())
    }

    fun logoutUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val auth = authData.value
            if(NetworkUtils.isInternetAvailable(application)) {
                if(auth != null){
                    val response = retrofitRepository.logoutUser(auth.id, auth.sessionId)
                    if(response.isSuccessful){
                        handleLogoutRoom()
                    } else if(response.errorBody() != null) {
                        ApiUtils.handleApiError(application, response.errorBody(), ::showMessage)
                    }
                }
            } else{
                handleLogoutRoom()
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(application, message, Toast.LENGTH_LONG).show()
    }

    private fun handleLogoutRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.delAuth()
            fireRepository.clearFires()
            val sharedPreferences = application.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("cameFromLogout", true).apply()
        }
    }
}
