package com.dripr.dripr.network

import com.dripr.dripr.entities.Event
import com.dripr.dripr.entities.Token
import retrofit2.http.*

interface EventsService {

    @GET("/events")
    suspend fun getAll(
        @Header("x-auth") token: Token
    ): List<Event>

    @GET("/events/search")
    suspend fun search(
        @Header("x-auth") token: Token,
        @QueryMap searchOptions: Map<String, String>
    ): List<Event>

    @POST("/events/{id}/join")
    suspend fun join(
        @Header("x-auth") token: Token,
        @Path("id") id: String
    ): Event

}