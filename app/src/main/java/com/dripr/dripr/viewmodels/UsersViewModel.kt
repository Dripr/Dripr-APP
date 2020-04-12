package com.dripr.dripr.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dripr.dripr.entities.Token
import com.dripr.dripr.repositories.UsersRepository
import com.dripr.dripr.entities.Event
import kotlinx.coroutines.Dispatchers


class UsersViewModel : ViewModel() {

    private val repository: UsersRepository = UsersRepository()

    fun getEvents(token: Token, onError: (ex: Exception) -> Unit): LiveData<List<Event>> = liveData(
        Dispatchers.IO) {
        try {
            val eventsList = repository.getEvents(token)
            emit(eventsList)
        } catch (ex: Exception) {
            onError(ex)
        }
    }
}