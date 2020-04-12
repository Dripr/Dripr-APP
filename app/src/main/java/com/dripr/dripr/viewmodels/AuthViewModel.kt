package com.dripr.dripr.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dripr.dripr.entities.User
import com.dripr.dripr.entities.payloads.LoginData
import com.dripr.dripr.entities.payloads.RegisterData
import com.dripr.dripr.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers

class AuthViewModel : ViewModel() {

    private val repository: AuthRepository = AuthRepository()

    fun login(loginData: LoginData, onError: (ex: Exception) -> Unit): LiveData<User> = liveData(Dispatchers.IO) {
        try {
            val loggedUser = repository.login(loginData)
            emit(loggedUser)
        } catch (ex: Exception) {
            onError(ex)
        }
    }

    fun register(registerData: RegisterData, onError: (ex: Exception) -> Unit): LiveData<User> = liveData(Dispatchers.IO) {
        try {
            val registeredUser = repository.register(registerData)
            emit(registeredUser)
        } catch (ex: Exception) {
            onError(ex)
        }
    }
}