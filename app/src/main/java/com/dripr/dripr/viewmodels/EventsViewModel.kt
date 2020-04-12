package com.dripr.dripr.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dripr.dripr.entities.Event
import com.dripr.dripr.entities.Token
import com.dripr.dripr.repositories.EventsRepository
import kotlinx.coroutines.Dispatchers


class EventsViewModel : ViewModel() {

    private val repository: EventsRepository = EventsRepository()

    fun getAll(token: Token, onError: (ex: Exception) -> Unit): LiveData<List<Event>> = liveData(
        Dispatchers.IO) {
        try {
            val eventsList = repository.getAll(token)
            emit(eventsList)
        } catch (ex: Exception) {
            onError(ex)
        }
    }

    fun search(token: Token, searchOptions: Map<String, String>, onError: (ex: Exception) -> Unit): LiveData<List<Event>> = liveData(Dispatchers.IO) {
        try {
            val eventsList = repository.search(token, searchOptions)
            emit(eventsList)
        } catch (ex: Exception) {
            onError(ex)
        }
    }

    fun join(token: Token, id: String, onError: (ex: Exception) -> Unit): LiveData<Event> = liveData(Dispatchers.IO) {
        try {
            val joinedEvent = repository.join(token, id)
            emit(joinedEvent)
        } catch (ex: Exception) {
            onError(ex)
        }
    }
}