package com.example.queimasegura.retrofit.repository

import com.example.queimasegura.retrofit.model.*
import com.example.queimasegura.retrofit.api.RetrofitInstance
import com.example.queimasegura.retrofit.model.get.Controller
import com.example.queimasegura.retrofit.model.get.CreateFire
import com.example.queimasegura.retrofit.model.get.CreateUser
import com.example.queimasegura.retrofit.model.get.Fire
import com.example.queimasegura.retrofit.model.get.FireDetails
import com.example.queimasegura.retrofit.model.get.Location
import com.example.queimasegura.retrofit.model.get.Login
import com.example.queimasegura.retrofit.model.get.Reasons
import com.example.queimasegura.retrofit.model.get.SimpleResponse
import com.example.queimasegura.retrofit.model.get.Types
import com.example.queimasegura.retrofit.model.get.UserStatus
import com.example.queimasegura.retrofit.model.send.CreateFireBody
import com.example.queimasegura.retrofit.model.send.CreateUserBody
import com.example.queimasegura.retrofit.model.send.LoginBody
import com.example.queimasegura.retrofit.util.MD5
import retrofit2.Response


class Repository {
    suspend fun getRoot(): Response<Root> {
        return RetrofitInstance.api.getRoot()
    }

    // STATIC
    suspend fun getTypes(): Response<Types> {
        return RetrofitInstance.api.getTypes()
    }

    suspend fun getReasons(): Response<Reasons> {
        return RetrofitInstance.api.getReasons()
    }

    suspend fun getController(): Response<Controller> {
        return RetrofitInstance.api.getController()
    }

    // AUTH
    suspend fun checkEmail(
        email: String
    ): Response<SimpleResponse> {
        return RetrofitInstance.api.checkEmail(email)
    }

    suspend fun checkSession(
        userId: String,
        sessionId: String
    ): Response<SimpleResponse> {
        return RetrofitInstance.api.checkSession(userId, sessionId)
    }

    suspend fun loginUser(
        loginBody: LoginBody
    ): Response<Login>{
        loginBody.password = MD5().getMD5Hash(loginBody.password)
        return RetrofitInstance.api.loginUser(loginBody)
    }

    suspend fun logoutUser(
        userId: String,
        sessionId: String
    ): Response<SimpleResponse> {
        return RetrofitInstance.api.logoutUser(userId, sessionId)
    }

    // USERS
    suspend fun createUser(
        createUserBody: CreateUserBody
    ): Response<CreateUser> {
        createUserBody.password = MD5().getMD5Hash(createUserBody.password)
        return RetrofitInstance.api.createUser(createUserBody)
    }

    suspend fun getUserStatus(
        userId: String,
        sessionId: String
    ): Response<UserStatus> {
        return RetrofitInstance.api.getUserStatus(userId, sessionId)
    }

    // FIRES
    suspend fun createFire(
        userId: String,
        sessionId: String,
        createFireBody: CreateFireBody
    ): Response<CreateFire> {
        return RetrofitInstance.api.createFire(userId, sessionId, createFireBody)
    }

    suspend fun getUserFires(
        userId: String,
        sessionId: String,
    ): Response<Fire> {
        return RetrofitInstance.api.getUserFires(userId, sessionId)
    }

    suspend fun getFireDetails(
        userId: String,
        fireId: String,
        sessionId: String
    ): Response<FireDetails> {
        return RetrofitInstance.api.getFireDetails(userId, fireId, sessionId)
    }

    suspend fun cancelFire(
        userId: String,
        fireId: String,
        sessionId: String
    ): Response<SimpleResponse> {
        return RetrofitInstance.api.cancelFire(userId, fireId, sessionId)
    }

    // LOCATION
    suspend fun getLocation(
        userId: String,
        sessionId: String,
        search: String
    ): Response<Location> {
        return RetrofitInstance.api.getLocation(userId, sessionId, search)
    }
    suspend fun getMapLocation(
        userId: String,
        sessionId: String,
        lat: Double,
        lng: Double
    ): Response<Location> {
        return RetrofitInstance.api.getMapLocation(userId, sessionId, lat, lng)
    }
}