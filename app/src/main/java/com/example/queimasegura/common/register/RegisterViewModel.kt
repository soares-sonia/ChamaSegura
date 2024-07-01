package com.example.queimasegura.common.register

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queimasegura.retrofit.model.get.CreateUser
import com.example.queimasegura.retrofit.model.send.CreateUserBody
import com.example.queimasegura.retrofit.model.get.SimpleResponse
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.repository.AuthRepository
import com.example.queimasegura.room.repository.FireRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewModel(
    private val application: Application,
    private val repository: Repository
): ViewModel() {
    private val _checkEmailResponse = MutableLiveData<Response<SimpleResponse>>()
    val checkEmailResponse: LiveData<Response<SimpleResponse>> get() = _checkEmailResponse
    private val _createUserResponse = MutableLiveData<Response<CreateUser>>()
    val createUserResponse: LiveData<Response<CreateUser>> get() = _createUserResponse

    private val authRepository: AuthRepository

    init {
        val appDataBase = AppDataBase.getDatabase(application)
        authRepository = AuthRepository(appDataBase.authDao())
    }

    fun checkEmail(
        email: String,
    ) {
        viewModelScope.launch {
            val response = repository.checkEmail(email)
            _checkEmailResponse.value = response
        }
    }

    fun createUser(
        createUserBody: CreateUserBody
    ) {
        viewModelScope.launch {
            val response = repository.createUser(createUserBody)
            _createUserResponse.value = response
            if(response.isSuccessful) {
                val resetJob = resetUserData()
                resetJob.join()
                response.body()?.result?.let {
                    val auth = Auth(
                        id = it.userId,
                        sessionId = it.sessionId,
                        email = createUserBody.email,
                        fullName = createUserBody.fullName,
                        nif = createUserBody.nif.toInt(),
                        type = 0
                    )
                    saveUser(auth)
                }
            }
        }
    }

    private fun saveUser(auth: Auth) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.authenticate(auth)
        }
    }

    private fun resetUserData() = viewModelScope.launch(Dispatchers.IO) {
        authRepository.delAuth()
    }
}