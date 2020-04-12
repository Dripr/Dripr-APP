package com.dripr.dripr.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dripr.dripr.activities.EventActivity
import com.dripr.dripr.R
import com.dripr.dripr.activities.SearchActivity
import com.dripr.dripr.adapters.EventAdapter
import com.dripr.dripr.entities.Token
import com.dripr.dripr.entities.User
import com.dripr.dripr.others.Utils.Companion.onError
import com.dripr.dripr.viewmodels.EventsViewModel
import com.dripr.dripr.entities.Event
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var eventsViewModel: EventsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        // View Model
        eventsViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)

        // Logic
        populateEvents()

        // Listeners
        v.homeRefresher.setOnRefreshListener { populateEvents() }
        v.homeSearch.setOnClickListener { goToSearchActivity() }
        return v
    }

    private fun populateEvents() {
        val token: Token = User.getFromDevice(this.requireContext()).tokens.last()!!
        val eventsLiveData: LiveData<List<Event>> = eventsViewModel.getAll(token) {
            onError(this.requireContext(), it)
            homeRefresher.isRefreshing = false
        }

        eventsLiveData.observe(this.requireActivity(), Observer { events: List<Event> ->
            val popularEvents = events.shuffled()
            val recentEvents = events.shuffled()
            setEvents(homePopularRecyclerView, popularEvents)
            setEvents(homeRecentRecyclerView, recentEvents)
            homeRefresher.isRefreshing = false
        })
    }

    private fun setEvents(rc: RecyclerView, events: List<Event>) = rc.apply {
        layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        adapter = EventAdapter("horizontal", events) { event -> onEventClick(event) }
    }

    private fun onEventClick(event: Event) {
        val intent = Intent(context, EventActivity::class.java)
        intent.putExtra("EVENT", event)
        startActivity(intent)
    }

    private fun goToSearchActivity() {
        startActivity(Intent(this.requireContext(), SearchActivity::class.java))
    }
}