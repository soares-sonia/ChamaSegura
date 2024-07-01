package com.example.queimasegura.common.detail.queimada

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.queimasegura.retrofit.model.get.FireDetails
import com.example.queimasegura.retrofit.model.get.SimpleResponse
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.repository.AuthRepository
import com.example.queimasegura.room.repository.FireRepository
import kotlinx.coroutines.launch
import retrofit2.Response


class QueimadaDetailsViewModel(
    private val application: Application,
    private val retrofitRepository: Repository
) : AndroidViewModel(application) {
    val authData: LiveData<Auth>
    private val _responseDetails = MutableLiveData<Response<FireDetails>>()
    val responseDetails: LiveData<Response<FireDetails>> get() = _responseDetails
    private val _cancelResponseDetails = MutableLiveData<Response<SimpleResponse>>()
    val cancelResponseDetails: LiveData<Response<SimpleResponse>> get() = _cancelResponseDetails

    private val authRepository: AuthRepository
    private val fireRepository: FireRepository

    init {
        val database = AppDataBase.getDatabase(application)
        authRepository = AuthRepository(database.authDao())
        authData = authRepository.readData
        fireRepository = FireRepository(database.fireDao())
    }

    fun fetchFireDetails(fireId: String, authUser: Auth) {
        viewModelScope.launch {
            val response = retrofitRepository.getFireDetails(authUser.id, fireId, authUser.sessionId)
            _responseDetails.value = response
        }
    }

    fun cancelFire(fireId: String, authUser: Auth) {
        viewModelScope.launch {
            val response = retrofitRepository.cancelFire(authUser.id, fireId, authUser.sessionId)
            _cancelResponseDetails.value = response
            if(response.isSuccessful) {
                fireRepository.removeFire(fireId)
            }
        }
    }
}