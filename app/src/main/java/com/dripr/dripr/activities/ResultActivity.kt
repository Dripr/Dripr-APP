package com.dripr.dripr.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dripr.dripr.R
import com.dripr.dripr.adapters.EventAdapter
import com.dripr.dripr.entities.Event
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    private lateinit var events: List<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        events = this.intent.getParcelableArrayListExtra<Event>("SEARCH_EVENTS").toList()

        resultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = EventAdapter("vertical", events) { event -> onEventClick(event) }
        }
    }

    private fun onEventClick(event: Event) {
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra("EVENT", event)
        startActivity(intent)
    }
}
