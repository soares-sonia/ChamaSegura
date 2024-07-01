package com.example.queimasegura.retrofit.api

import com.example.queimasegura.retrofit.model.*
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
import com.example.queimasegura.retrofit.model.send.CreateUserBody
import com.example.queimasegura.retrofit.model.send.CreateFireBody
import com.example.queimasegura.retrofit.model.send.LoginBody
import retrofit2.http.*
import retrofit2.Response


interface ApiService {
    @GET("/")
    suspend fun getRoot(): Response<Root>

    // STATIC
    @GET("/static/types")
    suspend fun getTypes(): Response<Types>

    @GET("/static/reasons")
    suspend fun getReasons(): Response<Reasons>

    @GET("/static/controller")
    suspend fun getController(): Response<Controller>

    // AUTH
    @GET("/auth/check_email")
    suspend fun checkEmail(
        @Query("email") email: String
    ): Response<SimpleResponse>

    @GET("/auth/check_session")
    suspend fun checkSession(
        @Query("user_id") userId: String,
        @Query("session_id") sessionId: String,
    ): Response<SimpleResponse>

    @POST("/auth/login")
    suspend fun loginUser(
        @Body loginBody: LoginBody
    ): Response<Login>

    @DELETE("/auth/logout")
    suspend fun logoutUser(
        @Query("user_id") userId: String,
        @Query("session_id") sessionId: String,
    ): Response<SimpleResponse>

    // USERS
    @POST("/users/create")
    suspend fun createUser(
        @Body createUserBody: CreateUserBody
    ): Response<CreateUser>

    @GET("/users/status/{user_id}")
    suspend fun getUserStatus(
        @Path("user_id") userId: String,
        @Query("session_id") sessionId: String
    ): Response<UserStatus>

    // FIRES
    @POST("/fires/{user_id}")
    suspend fun createFire(
        @Path("user_id") userId: String,
        @Query("session_id") sessionId: String,
        @Body createFireBody: CreateFireBody
    ): Response<CreateFire>

    @GET("/fires/{user_id}")
    suspend fun getUserFires(
        @Path("user_id") userId: String,
        @Query("session_id") sessionId: String,
    ): Response<Fire>

    @GET("/fires/{user_id}/{fire_id}")
    suspend fun getFireDetails(
        @Path("user_id") userId: String,
        @Path("fire_id") fireId: String,
        @Query("session_id") sessionId: String,
    ): Response<FireDetails>

    @DELETE("/fires/{user_id}/{fire_id}")
    suspend fun cancelFire(
        @Path("user_id") userId: String,
        @Path("fire_id") fireId: String,
        @Query("session_id") sessionId: String,
    ): Response<SimpleResponse>

    // LOCATION
    @GET("/location")
    suspend fun getLocation(
        @Query("user_id") userId: String,
        @Query("session_id") sessionId: String,
        @Query("search") search: String
    ): Response<Location>

    @GET("/location/map")
    suspend fun getMapLocation(
        @Query("user_id") userId: String,
        @Query("session_id") sessionId: String,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<Location>
}