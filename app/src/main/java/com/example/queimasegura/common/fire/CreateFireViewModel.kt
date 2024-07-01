package com.example.queimasegura.common.fire

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queimasegura.R
import com.example.queimasegura.retrofit.model.get.CreateFire
import com.example.queimasegura.retrofit.model.send.CreateFireBody
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.entities.Fire
import com.example.queimasegura.room.entities.Reason
import com.example.queimasegura.room.entities.Type
import com.example.queimasegura.room.repository.AuthRepository
import com.example.queimasegura.room.repository.FireRepository
import com.example.queimasegura.room.repository.StaticRepository
import com.example.queimasegura.room.repository.StatusRepository
import com.example.queimasegura.util.LocaleUtils
import com.example.queimasegura.util.NetworkUtils
import kotlinx.coroutines.launch
import retrofit2.Response


class CreateFireViewModel(
    private val application: Application,
    private val repository: Repository
) : ViewModel() {
    private val _createFireResponse = MutableLiveData<Response<CreateFire>>()
    val createFireResponse: LiveData<Response<CreateFire>> get () = _createFireResponse

    val typesData: LiveData<List<Type>>
    val reasonsData: LiveData<List<Reason>>
    private val authData: LiveData<Auth>
    private lateinit var authUser: Auth

    private val staticRepository: StaticRepository
    private val authRepository: AuthRepository
    private val statusRepository: StatusRepository
    private val fireRepository: FireRepository

    init {
        val database = AppDataBase.getDatabase(application)
        authRepository = AuthRepository(database.authDao())
        staticRepository = StaticRepository(
            database.controllerDao(), database.reasonDao(), database.typeDao()
        )
        statusRepository = StatusRepository(database.statusDao())
        fireRepository = FireRepository(database.fireDao())
        authData = authRepository.readData
        typesData = staticRepository.readTypesData
        reasonsData = staticRepository.readReasonsData

        observeAuth()
    }

    private fun observeAuth() {
        authRepository.readData.observeForever { auth ->
            auth?.let {
                authUser = it
            }
        }
    }

    fun createFire(
        createFireBody: CreateFireBody
    ) {
        if(NetworkUtils.isInternetAvailable(application)) {
            if(::authUser.isInitialized) {
                viewModelScope.launch {
                    val response = repository.createFire(authUser.id, authUser.sessionId, createFireBody)
                    _createFireResponse.value = response
                    if(response.isSuccessful) {
                        typesData.value?.forEach { type ->
                            if (type.id == createFireBody.typeId && type.namePt == "Queimada") {
                                statusRepository.addPending()
                            }
                        }
                        response.body()?.result?.let { result ->
                            val language = LocaleUtils.getUserPhoneLanguage(application)
                            val typeTranslated = if(language == "pt") result.typePt else result.typeEn
                            val statusTranslated = when (result.status) {
                                "Scheduled" -> application.getString(R.string.fire_status_scheduled)
                                "Ongoing" -> application.getString(R.string.fire_status_ongoing)
                                "Completed" -> application.getString(R.string.fire_status_completed)
                                "Pending" -> application.getString(R.string.fire_status_pending)
                                else -> result.status
                            }
                            val transformedDate = createFireBody.date.split("/")
                            fireRepository.addFire(Fire(
                                id = result.fireId,
                                type = typeTranslated,
                                status = statusTranslated,
                                date = "${transformedDate[2]}-${transformedDate[0]}-${transformedDate[1]}"
                            ))
                        }
                    }
                }
            }
        } else {
            Toast.makeText(application, application.getString(R.string.no_internet_available), Toast.LENGTH_SHORT).show()
        }
    }
}