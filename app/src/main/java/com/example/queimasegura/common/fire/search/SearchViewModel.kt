package com.example.queimasegura.common.fire.search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queimasegura.retrofit.model.get.Location
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.entities.ZipCode
import com.example.queimasegura.room.repository.AuthRepository
import com.example.queimasegura.room.repository.ZipCodeRepository
import com.example.queimasegura.util.NetworkUtils
import kotlinx.coroutines.launch
import retrofit2.Response


class SearchViewModel(
    private val application: Application,
    private val retrofitRepository: Repository
): ViewModel() {
    private val _locationResponse = MutableLiveData<Response<Location>>()
    val locationResponse: LiveData<Response<Location>> get() = _locationResponse

    private val authData: LiveData<Auth>
    private lateinit var authUser: Auth

    private val zipCodeRepository: ZipCodeRepository
    private val authRepository: AuthRepository

    init {
        val database = AppDataBase.getDatabase(application)
        authRepository = AuthRepository(database.authDao())
        zipCodeRepository = ZipCodeRepository(database.zipCodeDao())
        authData = authRepository.readData

        observeAuth()
    }

    private fun observeAuth() {
        authRepository.readData.observeForever { auth ->
            auth?.let {
                authUser = it
            }
        }
    }

    fun getLocation(search: String) {
        if(NetworkUtils.isInternetAvailable(application)) {
            viewModelScope.launch {
                if (::authUser.isInitialized) {
                    val response = retrofitRepository.getLocation(authUser.id, authUser.sessionId, search)
                    _locationResponse.value = response
                }
            }
        }
    }

    suspend fun getZips(): List<ZipCode>? {
        val zipCodes = zipCodeRepository.getZips()
        return zipCodes
    }

    fun saveLocation(zipcode: ZipCode) {
        viewModelScope.launch {
            zipCodeRepository.addZipcode(zipcode)
        }
    }
}