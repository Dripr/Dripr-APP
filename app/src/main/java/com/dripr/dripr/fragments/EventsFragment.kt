package com.dripr.dripr.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.dripr.dripr.R
import com.dripr.dripr.activities.EventActivity
import com.dripr.dripr.adapters.EventAdapter
import com.dripr.dripr.entities.Event
import com.dripr.dripr.entities.Token
import com.dripr.dripr.entities.User
import com.dripr.dripr.others.Utils.Companion.onError
import com.dripr.dripr.viewmodels.UsersViewModel
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.dialog_info.*
import kotlinx.android.synthetic.main.fragment_events.view.*


class EventsFragment : Fragment() {

    private lateinit var usersViewModel: UsersViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_events, container, false)

        // View Model
        usersViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)

        // Logic
        setUserName(v)
        setProfilePictures(v)
        setBottomSheet(v)
        getEvents(v)

        // Listeners
        v.apply {
            profileInviteFriend.setOnClickListener { inviteFriends() }
            profileGetHelp.setOnClickListener { showCustomDialog() }
            profileGiveFeedback.setOnClickListener { sendFeedback() }
        }
        return v
    }

    private fun getEvents(v: View) {
        val token: Token = User.getFromDevice(this.requireContext()).tokens.last()!!
        val eventsLiveData: LiveData<List<Event>> = usersViewModel.getEvents(token) { onError(this.requireContext(), it) }

        eventsLiveData.observe(this.requireActivity(), Observer {
            v.userEvents.apply {
                layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
                adapter = EventAdapter("vertical", it) { event -> onEventClick(event) }
            }
        })
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setUserName(v: View) {
        val user: User = User.getFromDevice(this.requireContext())
        v.eventsUsername.text = "${user.firstName.capitalize()}.${user.lastName[0].toUpperCase()}"
    }

    private fun setImage(url: GlideUrl, options: RequestOptions, view: ImageView) = Glide
        .with(this)
        .load(url)
        .apply(options)
        .into(view)


    private fun setBottomSheet(v: View) {
        val llBottomSheet = v.bottom_sheet as LinearLayout
        val bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
        })
        v.eventsProfilePicture.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun setProfilePictures(v: View) {
        val token = User.getFromDevice(this.requireContext()).tokens.last()?.token ?: ""
        val pictureUrl = User.getFromDevice(this.requireContext()).profilePicture
        val glideUrl = GlideUrl(pictureUrl) { mapOf(Pair("x-auth", token)) }
        val options = RequestOptions.circleCropTransform()

        setImage(glideUrl, options, v.eventsProfilePicture)
        setImage(glideUrl, options, v.eventsBottomProfilePicture)
    }

    private fun onEventClick(event: Event) {
        val intent = Intent(context, EventActivity::class.java)
        intent.putExtra("EVENT", event)
        startActivity(intent)
    }

    private fun inviteFriends() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
        startActivity(Intent.createChooser(shareIntent, "Send to ..."))
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this.requireContext())
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

    private fun sendFeedback() {
        val to = "test@gmail.com"
        val subject = "Hi I am subject"
        val body = "Hi I am test body"
        val mailTo = "mailto:" + to +
                "?&subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(body)
        val emailIntent = Intent(Intent.ACTION_VIEW)
        emailIntent.data = Uri.parse(mailTo)
        startActivity(emailIntent)
    }
}