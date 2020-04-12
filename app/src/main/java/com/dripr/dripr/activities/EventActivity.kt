package com.dripr.dripr.activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dripr.dripr.R
import com.dripr.dripr.entities.Token
import com.dripr.dripr.entities.User
import com.dripr.dripr.others.Utils.Companion.onError
import com.dripr.dripr.viewmodels.EventsViewModel
import com.dripr.dripr.entities.Event
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.dialog_info.*


class EventActivity : AppCompatActivity() {

    private lateinit var eventsViewModel: EventsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        // View Model
        eventsViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)

        // Logic
        val event: Event? = intent.getParcelableExtra("EVENT") ?: null
        if (event != null) {
            setEvent(event)

            // Listeners
            eventMembers.setOnClickListener { showCustomDialog() }
            eventCalendar.setOnClickListener { showCustomDialog() }
            eventGmaps.setOnClickListener { startNavigation(event) }
            eventJoin.setOnClickListener { joinEvent(event) }
        } else finish()
    }

    private fun setEvent(event: Event) {
        val memberList = event.members
        val userId: String = User.getFromDevice(this).id
        val result = memberList.find { user -> user?.id == userId }

        if (result != null) { eventJoin.visibility= View.GONE }

        Glide.with(this).load(event.cover).into(eventCover)

        eventTitle.text = event.title
        eventDescription.text = event.description
        eventAddress.text = event.address.placeName
    }

    private fun joinEvent(event: Event) {
        val token: Token = User.getFromDevice(this).tokens.last()!!
        val eventId: String = event.id
        val eventsLiveData: LiveData<Event> = eventsViewModel.join(token, eventId) { onError(this, it) }

        eventsLiveData.observe(this, Observer {
            eventJoin.hide()
            eventJoin.visibility = View.GONE
        })
    }

    private fun startNavigation(event: Event) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("google.navigation:q=${event.address.placeName}")
        )
        startActivity(intent)
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_info)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.bt_close.setOnClickListener {
            // Do stuff on close
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }
}
