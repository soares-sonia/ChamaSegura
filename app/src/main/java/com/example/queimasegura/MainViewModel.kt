package com.example.queimasegura

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.db.AppDataBase
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.entities.Controller
import com.example.queimasegura.room.entities.Fire
import com.example.queimasegura.room.entities.Type
import com.example.queimasegura.room.entities.Reason
import com.example.queimasegura.room.repository.AuthRepository
import com.example.queimasegura.room.repository.FireRepository
import com.example.queimasegura.room.repository.StaticRepository
import com.example.queimasegura.util.ApiUtils
import com.example.queimasegura.util.LocaleUtils
import com.example.queimasegura.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(
    private val application: Application,
    private val retrofitRepository: Repository
) : ViewModel() {
    enum class AppState {
        INTRO, HOME_USER, HOME_MANAGER, HOME_ADMIN, LOGIN, ERROR
    }
    private val _appState = MutableLiveData<AppState>()
    val appState: LiveData<AppState> get() = _appState
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage
    var isAppStarted: Boolean = false

    val authData: LiveData<Auth>

    private val authRepository: AuthRepository
    private val staticRepository: StaticRepository
    private val fireRepository: FireRepository

    init {
        val database = AppDataBase.getDatabase(application)
        authRepository = AuthRepository(database.authDao())
        staticRepository = StaticRepository(
            database.controllerDao(),
            database.reasonDao(),
            database.typeDao()
        )
        fireRepository = FireRepository(database.fireDao())
        authData = authRepository.readData
    }


    fun startApp() {
        isAppStarted = true
        viewModelScope.launch(Dispatchers.IO) {
            val isInternetAvailable = NetworkUtils.isInternetAvailable(application)
            val auth = authData.value
            val controller = staticRepository.getController()

            handleInternetAvailability(isInternetAvailable, auth, controller)
        }
    }

    fun firstRun() {
        isAppStarted = true
        viewModelScope.launch(Dispatchers.IO) {
            val isInternetAvailable = NetworkUtils.isInternetAvailable(application)
            val auth = authData.value
            val controller = staticRepository.getController()

            if(isInternetAvailable) {
                handleController(controller)
                _appState.postValue(AppState.INTRO)
            } else {
                handleOfflineMode(auth, controller)
            }
        }
    }

    private suspend fun handleInternetAvailability(
        isInternetAvailable: Boolean,
        auth: Auth?,
        controller: Controller?
    ) {
        if(isInternetAvailable) {
            handleController(controller)
            handleAuth(auth)
            handleFires(auth)
        } else {
            handleOfflineMode(auth, controller)
        }
    }

    private fun handleController(controller: Controller?) {
        if (controller == null) {
            updateStaticData()
        } else {
            checkController(controller) { isSameController ->
                if (!isSameController) {
                    updateStaticData()
                }
            }
        }
    }

    private suspend fun handleAuth(auth: Auth?) {
        if (auth == null) {
            _appState.postValue(AppState.LOGIN)
        } else {
            checkSession(auth.id, auth.sessionId) { isSameSession ->
                if (isSameSession) {
                    when (auth.type) {
                        0 -> _appState.postValue(AppState.HOME_USER)
                        1 -> _appState.postValue(AppState.HOME_MANAGER)
                        2 -> _appState.postValue(AppState.HOME_ADMIN)
                    }
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        deleteAuthAndRedirectToLogin()
                    }
                }
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

    private suspend fun deleteAuthAndRedirectToLogin() {
        authRepository.delAuth()
        _appState.postValue(AppState.LOGIN)
        _errorMessage.postValue(application.getString(R.string.main_error_login))
    }

    private fun handleOfflineMode(auth: Auth?, controller: Controller?) {
        if (controller == null) {
            _appState.postValue(AppState.ERROR)
            _errorMessage.postValue(application.getString(R.string.main_error_controller))
        } else if (auth == null) {
            _appState.postValue(AppState.ERROR)
            _errorMessage.postValue(application.getString(R.string.main_error_auth))
        } else {
            when (auth.type) {
                0 -> _appState.postValue(AppState.HOME_USER)
                1 -> _appState.postValue(AppState.HOME_MANAGER)
                2 -> _appState.postValue(AppState.HOME_ADMIN)
            }
        }
    }

    private fun checkController(
        roomController: Controller,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val controllerResponse = retrofitRepository.getController()
            if(controllerResponse.isSuccessful) {
                val controllerData = controllerResponse.body()?.result
                if (controllerData != null && controllerData.id == roomController.id && controllerData.name == roomController.name) {
                    callback(true)
                } else{
                    callback(false)
                }
            } else {
                callback(false)
            }
        }
    }

    private fun checkSession(
        userId: String,
        sessionId: String,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionResponse = retrofitRepository.checkSession(userId, sessionId)
            if(sessionResponse.isSuccessful) {
                callback(true)
            } else{
                callback(false)
            }
        }
    }

    private fun updateStaticData() {
        viewModelScope.launch(Dispatchers.IO) {
            val controllerResponse = retrofitRepository.getController()
            if(controllerResponse.isSuccessful) {
                staticRepository.clearController()
                val controllerData = controllerResponse.body()?.result
                if (controllerData != null) {
                    staticRepository.addController(Controller(
                        id = controllerData.id,
                        name = controllerData.name
                    ))
                }
            }

            val typesResponse = retrofitRepository.getTypes()
            if(typesResponse.isSuccessful) {
                staticRepository.clearTypes()
                typesResponse.body()?.result?.forEach { typeData ->
                    staticRepository.addType(
                        Type(
                            id = typeData.id,
                            namePt = typeData.namePt,
                            nameEn = typeData.nameEn
                        )
                    )
                }
            }

            val reasonsResponse = retrofitRepository.getReasons()
            if(reasonsResponse.isSuccessful) {
                staticRepository.clearReasons()
                reasonsResponse.body()?.result?.forEach { reasonData ->
                    staticRepository.addReason(
                        Reason(
                            id = reasonData.id,
                            namePt = reasonData.namePt,
                            nameEn = reasonData.nameEn
                        )
                    )
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
