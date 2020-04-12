package com.dripr.dripr.repositories

import com.dripr.dripr.entities.Event
import com.dripr.dripr.entities.Token
import com.dripr.dripr.network.ApiClient

class EventsRepository {

    private val eventsClient = ApiClient.eventsService

    suspend fun getAll(token: Token): List<Event> = eventsClient.getAll(token)
    suspend fun search(token: Token, searchOptions: Map<String, String>): List<Event> = eventsClient.search(token, searchOptions)
    suspend fun join(token: Token, id: String): Event = eventsClient.join(token, id)
}