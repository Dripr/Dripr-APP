package com.dripr.dripr.repositories

import com.dripr.dripr.entities.Token
import com.dripr.dripr.network.ApiClient
import com.dripr.dripr.entities.Event

class UsersRepository {

    private val eventsClient = ApiClient.usersService

    suspend fun getEvents(token: Token): List<Event> = eventsClient.getEvents(token)
}