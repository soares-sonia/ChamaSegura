package com.example.queimasegura.admin.fragments.home

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.queimasegura.retrofit.repository.AdminRepository
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.entities.Fire
import com.example.queimasegura.room.entities.Status
import com.example.queimasegura.room.repository.AuthRepository
import com.example.queimasegura.room.repository.FireRepository
import com.example.queimasegura.room.repository.StatusRepository
import com.example.queimasegura.util.ApiUtils
import com.example.queimasegura.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeViewModel (
    private val application: Application,
    private val retrofitRepository: Repository,
    private val adminRetrofitRepository: AdminRepository
) : AndroidViewModel(application) {
    val statusData: LiveData<Status>
    val authData: LiveData<Auth>

    private val authRepository: AuthRepository
    private val statusRepository: StatusRepository

    init {
        val database = AppDataBase.getDatabase(application)
        authRepository = AuthRepository(database.authDao())
        authData = authRepository.readData

        statusRepository = StatusRepository(database.statusDao())
        statusData = statusRepository.readData

        observeAuthData()
    }

    private fun observeAuthData() {
        authData.observeForever { auth ->
            auth?.let {
                fetchUserStatus(it)
            }
        }
    }

    private fun fetchUserStatus(auth: Auth) {
        viewModelScope.launch(Dispatchers.IO) {
            val isInternetAvailable = NetworkUtils.isInternetAvailable(application)
            if(isInternetAvailable) {
                val response = adminRetrofitRepository.adminGetStatus(auth.id, auth.sessionId)
                if(response.isSuccessful) {
                    response.body()?.let { userStatus ->
                        val status = Status(
                            id = 0,
                            firesPending = userStatus.result.firesWaiting,
                            firesComplete = userStatus.result.firesApproved
                        )
                        statusRepository.clearStatus()
                        statusRepository.addStatus(status)
                    }
                } else if(response.errorBody() != null) {
                    ApiUtils.handleApiError(application, response.errorBody(), ::showMessage)
                }
            }
        }
    }

    private fun showMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                Toast.makeText(application, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
