package com.example.queimasegura.admin.fragments.users

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.queimasegura.retrofit.repository.AdminRepository
import com.example.queimasegura.retrofit.repository.Repository

class UsersViewModelFactory (
    private val application: Application,
    private val repository: Repository,
    private val adminRepository: AdminRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UsersViewModel(application, repository, adminRepository) as T
    }
}