package com.dripr.dripr.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {

    companion object {
        private var client: Retrofit = Retrofit.Builder()
            .baseUrl("https://sevenchill-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var authService: AuthService = client.create(AuthService::class.java)
        var usersService: UsersService = client.create(UsersService::class.java)
        var eventsService: EventsService = client.create(EventsService::class.java)
    }
}