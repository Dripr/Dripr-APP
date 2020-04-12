package com.dripr.dripr.repositories

import com.dripr.dripr.entities.User
import com.dripr.dripr.entities.payloads.LoginData
import com.dripr.dripr.entities.payloads.RegisterData
import com.dripr.dripr.network.ApiClient

class AuthRepository {

    private val authClient = ApiClient.authService

    suspend fun login(data: LoginData): User = authClient.login(data)

    suspend fun register(data: RegisterData): User = authClient.register(data)
}