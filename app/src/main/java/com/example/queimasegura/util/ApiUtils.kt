package com.example.queimasegura.util

import android.content.Context
import android.util.Log
import com.example.queimasegura.R
import com.example.queimasegura.retrofit.model.ErrorApi
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.repository.AuthRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.IOException

object ApiUtils {

    fun handleApiError(
        context: Context,
        errorBody: ResponseBody?,
        showMessage: (String) -> Unit
    ) {
        if (errorBody != null) {
            try {
                val gson = Gson()
                val type = object : TypeToken<ErrorApi>() {}.type
                val errorApiResponse: ErrorApi? = gson.fromJson(errorBody.charStream(), type)
                errorApiResponse?.let {
                    if (it.detail == "Session does not match" || it.detail == "Session does not exist") {
                        deleteAuthData(context)
                    } else {
                        showMessage(it.detail)
                    }
                }
            } catch (e: IOException) {
                showMessage(context.getString(R.string.server_error))
            }
        } else {
            showMessage(context.getString(R.string.server_error))
        }
    }

    private fun deleteAuthData(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDataBase.getDatabase(context)
            val authRepository = AuthRepository(database.authDao())
            authRepository.delAuth()
        }
    }
}
