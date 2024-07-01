package com.example.queimasegura.admin.fragments.home.fire.map

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.queimasegura.retrofit.repository.Repository


class MapViewModelFactory(
    private val application: Application,
    private val repository: Repository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  MapViewModel(application, repository) as T
    }
}