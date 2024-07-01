package com.example.queimasegura.retrofit.api

import com.example.queimasegura.retrofit.model.Root
import com.example.queimasegura.retrofit.model.admin.get.AdminGetUsers
import com.example.queimasegura.retrofit.model.admin.get.AdminSearchUser
import com.example.queimasegura.retrofit.model.admin.get.AdminStatus
import com.example.queimasegura.retrofit.model.get.SimpleResponse
import com.example.queimasegura.retrofit.model.send.CreateFireBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface AdminApiService {
    @GET("/")
    suspend fun getRoot(): Response<Root>

    @GET("/admin/status")
    suspend fun adminGetStatus(
        @Query("admin_id") adminId: String,
        @Query("session_id") sessionId: String,
    ): Response<AdminStatus>

    @GET("/admin/users")
    suspend fun adminGetUsers(
        @Query("admin_id") adminId: String,
        @Query("session_id") sessionId: String,
    ): Response<AdminGetUsers>

    @PATCH("/admin/ban/{user_id}")
    suspend fun banUser(
        @Path("user_id") userId: String,
        @Query("admin_id") adminId: String,
        @Query("session_id") sessionId: String,
    ): Response<SimpleResponse>

    @PATCH("/admin/unban/{user_id}")
    suspend fun unbanUser(
        @Path("user_id") userId: String,
        @Query("admin_id") adminId: String,
        @Query("session_id") sessionId: String,
    ): Response<SimpleResponse>

    @DELETE("/admin/users/{user_id}")
    suspend fun deleteUser(
        @Path("user_id") userId: String,
        @Query("admin_id") adminId: String,
        @Query("session_id") sessionId: String,
    ): Response<SimpleResponse>

    @PATCH("/admin/users/{user_id}")
    suspend fun restoreUser(
        @Path("user_id") userId: String,
        @Query("admin_id") adminId: String,
        @Query("session_id") sessionId: String,
    ): Response<SimpleResponse>

    @PATCH("/admin/users/{user_id}/{perm}")
    suspend fun editUserPerms(
        @Path("user_id") userId: String,
        @Path("perm") perm: Int,
        @Query("admin_id") adminId: String,
        @Query("session_id") sessionId: String,
    ): Response<SimpleResponse>

    @GET("/admin/users/{search}")
    suspend fun searchUser(
        @Path("search") search: String,
        @Query("admin_id") adminId: String,
        @Query("session_id") sessionId: String,
    ): Response<AdminSearchUser>

    @POST("/admin/fire")
    suspend fun createFire(
        @Query("admin_id") adminId: String,
        @Query("session_id") sessionId: String,
        @Query("user_id") userId: String,
        @Body createFireBody: CreateFireBody
    ): Response<SimpleResponse>
}