package com.example.queimasegura.admin.fragments.users

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queimasegura.retrofit.model.admin.get.AdminGetUsers
import com.example.queimasegura.retrofit.repository.AdminRepository
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Response


class UsersViewModel(
    private val application: Application,
    private val retrofitRepository: Repository,
    private val adminRetrofitRepository: AdminRepository
): ViewModel()  {
    private val _usersResponse = MutableLiveData<Response<AdminGetUsers>> ()
    val usersResponse: LiveData<Response<AdminGetUsers>> get() = _usersResponse

    val authData: LiveData<Auth>

    private val authRepository: AuthRepository

    init {
        val database = AppDataBase.getDatabase(application)
        authRepository = AuthRepository(database.authDao())
        authData = authRepository.readData
    }

     fun fetchUsersData(auth: Auth) {
        viewModelScope.launch {
            val response = adminRetrofitRepository.adminGetUsers(auth.id, auth.sessionId)
            _usersResponse.value = response
        }
    }

    fun banUser(auth: Auth, userId: String) {
        viewModelScope.launch {
            val response = adminRetrofitRepository.banUser(userId, auth.id, auth.sessionId)
        }
    }

    fun unbanUser(auth: Auth, userId: String) {
        viewModelScope.launch {
            val response = adminRetrofitRepository.unbanUser(userId, auth.id, auth.sessionId)
        }
    }

    fun deleteUser(auth: Auth, userId: String) {
        viewModelScope.launch {
            val response = adminRetrofitRepository.deleteUser(userId, auth.id, auth.sessionId)
        }
    }

    fun restoreUer(auth: Auth, userId: String) {
        viewModelScope.launch {
            val response = adminRetrofitRepository.restoreUser(userId, auth.id, auth.sessionId)
        }
    }

    fun editUserPerms(auth: Auth, userId: String, perm: Int) {
        viewModelScope.launch {
            val response = adminRetrofitRepository.editUserPerms(userId, perm, auth.id, auth.sessionId)
        }
    }
}