package com.dripr.dripr.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.dripr.dripr.activities.EventActivity
import com.dripr.dripr.R
import com.dripr.dripr.adapters.EventAdapter
import com.dripr.dripr.entities.Token
import com.dripr.dripr.entities.User
import com.dripr.dripr.others.Utils.Companion.onError
import com.dripr.dripr.viewmodels.EventsViewModel
import com.dripr.dripr.entities.Event
import com.dripr.dripr.others.OnSnapPositionChangeListener
import com.dripr.dripr.others.SnapOnScrollListener
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.fragment_explore.view.*


class ExploreFragment : Fragment(), OnMapReadyCallback, PermissionListener {

    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
    }

    private var isDark = true
    private lateinit var map: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var events: List<Event>
    private val catIconsMap = HashMap<String, Int>()
    private lateinit var eventsViewModel: EventsViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_explore, container, false)

        // View Model
        eventsViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)

        // Map
        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fusedLocationProviderClient = FusedLocationProviderClient(this.requireActivity())
        map = v.findViewById(R.id.exploreMap) as MapView
        map.onCreate(savedInstanceState)
        map.onResume()
        map.getMapAsync(this)

        // Listeners
        v.toggleStyle.setOnClickListener { toggleMapTheme() }
        return v
    }

    private fun toggleMapTheme() {
        isDark = if(isDark) setStyle(R.raw.style_light_json, false) else setStyle(R.raw.style_dark_json, true)
    }

    private fun setStyle(styleId: Int, isThemeDark: Boolean): Boolean {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.activity, styleId))
        return isThemeDark
    }

    private fun onEventClick(event: Event) {
        val intent = Intent(context, EventActivity::class.java)
            intent.putExtra("EVENT", event)
            startActivity(intent)
    }

    override fun onMapReady(mMap: GoogleMap) {
        googleMap = mMap
        setStyle(R.raw.style_dark_json, true)
        addAllMarkers()
        checkPermission()
        setOnMarkerClickListener()
        addSydneyMarker()
        setSnapHelperBehaviorForRecyclerView()
    }

    private fun addAllMarkers() {
        val token: Token = User.getFromDevice(this.requireContext()).tokens.last()!!
        val eventsLiveData: LiveData<List<Event>> = eventsViewModel.getAll(token) { onError(this.requireContext(), it)
        }

        eventsLiveData.observe(this.requireActivity(), Observer {
            events = it
            setEvents(eventsRcView)
            populateMap()
        })
    }

    private fun setEvents(rc: RecyclerView) = rc.apply {
        layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        adapter = EventAdapter("map", events) { event -> onEventClick(event) }
    }

    @SuppressLint("Recycle")
    private fun populateMap() = events.forEach { event ->

        val icons = resources.obtainTypedArray(R.array.eventsIcons)
        val categories = resources.getStringArray(R.array.eventsCategories)

        for (i in categories.indices) catIconsMap[categories[i]] = icons.getResourceId(i, -1)

        val drawableRef = catIconsMap[event.category] ?: R.drawable.ic_location_white

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(event.address.latitude, event.address.longitude))
                .title(event.title)
                .snippet(event.description)
                .icon(bitmapDescriptorFromVector(this.requireActivity(), drawableRef))
        )
    }

    private fun addSydneyMarker() {
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun checkPermission() {
        if (isPermissionGiven()){
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            getCurrentLocation()
        } else {
            givePermission()
        }
    }

    private fun setSnapHelperBehaviorForRecyclerView() {
        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(eventsRcView)
        val behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL
        val onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                val event = events[position]
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(event.address.latitude, event.address.longitude)))
            }
        }
        val snapOnScrollListener = SnapOnScrollListener(helper, behavior, onSnapPositionChangeListener)
        eventsRcView.addOnScrollListener(snapOnScrollListener)
    }

    private fun setOnMarkerClickListener() {
        googleMap.setOnMarkerClickListener {
//            val id = m.id.removePrefix("m").toInt()
//            linearLayoutManager.scrollToPosition(id)
//            googleMap.animateCamera(CameraUpdateFactory.newLatLng(events[id].location.coords))
            true
        }
    }

    @SuppressLint("RestrictedApi")
    private fun getCurrentLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        val locationSettingsRequest = builder.build()

        val result = LocationServices.getSettingsClient(this.requireActivity()).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent){
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this.activity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
                }
            }
        }
    }

    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(this.requireActivity()) { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                val lastLocation: Location = task.result
                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(lastLocation.latitude, lastLocation.longitude))
                    .zoom(17f)
                    .build()
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            } else Toast.makeText(this.requireContext(), "No current location found", Toast.LENGTH_LONG).show()
        }
    }

    private fun givePermission() = Dexter.withActivity(this.activity)
        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        .withListener(this)
        .check()

    private fun isPermissionGiven(): Boolean = ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) = getCurrentLocation()

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) = token!!.continuePermissionRequest()

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(this.context, "Permission required for showing location", Toast.LENGTH_LONG).show()
        activity?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> if (resultCode == Activity.RESULT_OK) getCurrentLocation()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}