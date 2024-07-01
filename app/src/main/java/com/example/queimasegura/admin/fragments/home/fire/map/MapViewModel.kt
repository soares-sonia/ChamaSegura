package com.example.queimasegura.admin.fragments.home.fire.map

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queimasegura.R
import com.example.queimasegura.retrofit.model.data.Location
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.entities.ZipCode
import com.example.queimasegura.room.repository.AuthRepository
import com.example.queimasegura.room.repository.ZipCodeRepository
import com.example.queimasegura.util.ApiUtils
import kotlinx.coroutines.launch


class MapViewModel(
    private val application: Application,
    private val retrofitRepository: Repository
) : ViewModel() {
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

    fun getMapLocation(lat: Double, lng: Double, handleSendLocation: (Location) -> Unit) {
        viewModelScope.launch {
            if(::authUser.isInitialized) {
                val response = retrofitRepository.getMapLocation(authUser.id, authUser.sessionId, lat, lng)
                if(response.isSuccessful) {
                    response.body()?.result.let { locations ->
                        val location =  locations?.get(0)
                        if(location != null) {
                            handleSendLocation(location)
                            zipCodeRepository.addZipcode(ZipCode(
                                id = location.id,
                                locationName = location.locationName,
                                zipCode = location.zipCode,
                                artName = location.artName,
                                tronco = location.tronco
                            ))
                        }
                    }
                } else if(response.errorBody() != null) {
                    ApiUtils.handleApiError(application, response.errorBody(), ::showMessage)
                } else{
                    showMessage(application.getString(R.string.server_error))
                }
            }
        }
    }

    private fun showMessage(str: String) {
        Toast.makeText(application, str, Toast.LENGTH_LONG).show()
    }
}