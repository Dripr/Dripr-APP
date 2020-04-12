package com.dripr.dripr.network

import com.dripr.dripr.entities.User
import com.dripr.dripr.entities.payloads.LoginData
import com.dripr.dripr.entities.payloads.RegisterData
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("/auth/login")
    suspend fun login(@Body loginData: LoginData): User

    @POST("/auth/register")
    suspend fun register(@Body registerData: RegisterData): User
}