package com.example.queimasegura.admin.fragments.home.fire.search.user

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queimasegura.retrofit.model.admin.get.AdminSearchUser
import com.example.queimasegura.retrofit.repository.AdminRepository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.repository.AuthRepository
import com.example.queimasegura.util.NetworkUtils
import kotlinx.coroutines.launch
import retrofit2.Response


class SearchUserViewModel(
    private val application: Application,
    private val adminRetrofitRepository: AdminRepository
): ViewModel() {
    private val _locationResponse = MutableLiveData<Response<AdminSearchUser>>()
    val locationResponse: LiveData<Response<AdminSearchUser>> get() = _locationResponse

    private val authData: LiveData<Auth>
    private lateinit var authUser: Auth

    private val authRepository: AuthRepository

    init {
        val database = AppDataBase.getDatabase(application)
        authRepository = AuthRepository(database.authDao())
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

    fun getUsers(search: String) {
        if(NetworkUtils.isInternetAvailable(application)) {
            viewModelScope.launch {
                if (::authUser.isInitialized) {
                    val response = adminRetrofitRepository.searchUser(search, authUser.id, authUser.sessionId)
                    _locationResponse.value = response
                }
            }
        }
    }
}