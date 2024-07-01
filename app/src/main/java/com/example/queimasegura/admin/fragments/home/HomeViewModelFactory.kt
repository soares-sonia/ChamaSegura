package com.example.queimasegura.admin.fragments.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.queimasegura.retrofit.repository.AdminRepository
import com.example.queimasegura.retrofit.repository.Repository


class HomeViewModelFactory (
    private val application: Application,
    private val repository: Repository,
    private val adminRetrofitRepository: AdminRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(application, repository, adminRetrofitRepository) as T
    }
}