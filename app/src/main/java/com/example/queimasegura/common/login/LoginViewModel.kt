package com.example.queimasegura.common.login

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queimasegura.R
import com.example.queimasegura.admin.AdminActivity
import com.example.queimasegura.manager.ManagerActivity
import com.example.queimasegura.retrofit.model.get.Login
import com.example.queimasegura.retrofit.model.send.LoginBody
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.entities.Fire
import com.example.queimasegura.room.repository.AuthRepository
import com.example.queimasegura.room.repository.FireRepository
import com.example.queimasegura.user.UserActivity
import com.example.queimasegura.util.ApiUtils
import com.example.queimasegura.util.LocaleUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class LoginViewModel(
    private val application: Application,
    private val retrofitRepository: Repository
): ViewModel() {
    private val _auth = MutableLiveData<Auth>()
    val auth: LiveData<Auth> get() = _auth

    private val authRepository: AuthRepository
    private val fireRepository: FireRepository

    init {
        val database = AppDataBase.getDatabase(application)
        authRepository = AuthRepository(database.authDao())
        fireRepository = FireRepository(database.fireDao())
    }

    suspend fun loginUser(loginBody: LoginBody) {
        viewModelScope.launch {
            val response = retrofitRepository.loginUser(loginBody)
            if(response.isSuccessful) {
                val resetJob = resetUserData()
                resetJob.join()
                response.body()?.result?.let {
                    val auth = Auth(
                        id = it.user.id,
                        sessionId = it.sessionId,
                        email = loginBody.email,
                        fullName = it.user.fullName,
                        nif = it.user.nif,
                        type = it.user.type
                    )
                    saveUser(auth)
                    handleFires(auth)
                    _auth.postValue(auth)
                }
            } else if(response.errorBody() != null) {
                ApiUtils.handleApiError(application, response.errorBody(), ::showMessage)
            } else{
                showMessage(application.getString(R.string.server_error))
            }
        }
    }

    private suspend fun handleFires(auth: Auth?) {
        if(auth != null) {
            val response = retrofitRepository.getUserFires(auth.id, auth.sessionId)
            if(response.isSuccessful) {
                val language = LocaleUtils.getUserPhoneLanguage(application)
                fireRepository.clearFires()
                response.body()?.result?.forEach { result ->
                    val statusTranslated = when (result.status) {
                        "Scheduled" -> application.getString(R.string.fire_status_scheduled)
                        "Ongoing" -> application.getString(R.string.fire_status_ongoing)
                        "Completed" -> application.getString(R.string.fire_status_completed)
                        "Pending" -> application.getString(R.string.fire_status_pending)
                        "Approved" -> application.getString(R.string.fire_status_approved)
                        "Not Approved" -> application.getString(R.string.fire_status_refuse)
                        else -> result.status
                    }

                    fireRepository.addFire(Fire(
                        id = result.id,
                        date = result.date,
                        status = statusTranslated,
                        type = if(language == "pt") result.typePt else result.typeEn
                    ))
                }
            } else if(response.errorBody() != null) {
                ApiUtils.handleApiError(application, response.errorBody(), ::showMessage)
            } else{
                showMessage(application.getString(R.string.server_error))
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

    private fun showMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                Toast.makeText(application, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}