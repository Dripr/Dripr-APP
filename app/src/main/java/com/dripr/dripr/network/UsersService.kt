package com.dripr.dripr.network

import com.dripr.dripr.entities.Token
import com.dripr.dripr.entities.Event
import retrofit2.http.GET
import retrofit2.http.Header

interface UsersService {

    @GET("/users/{id}/events")
    suspend fun getEvents(
        @Header("x-auth") token: Token
    ): List<Event>
}