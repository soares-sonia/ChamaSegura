package com.example.queimasegura.admin.fragments.home.fire.map

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.queimasegura.R
import com.example.queimasegura.admin.fragments.home.fire.CreateFireActivity
import com.example.queimasegura.admin.fragments.home.fire.model.CreateFireDataIntent
import com.example.queimasegura.admin.fragments.home.fire.model.UserIntent
import com.example.queimasegura.admin.fragments.home.fire.model.ZipcodeIntent
import com.example.queimasegura.retrofit.model.data.Location
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.util.NetworkUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var viewModel: MapViewModel
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var userMarker: Marker? = null
    private val portugalBounds = LatLngBounds(
        LatLng(36.9614, -9.5000),
        LatLng(42.1543, -6.1892)
    )
    private var coords: LatLng? = null

    private lateinit var parentData: CreateFireDataIntent
    private lateinit var userData: UserIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reqPermsMapPage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        initViewModels()

        initIntents()

        initEvents()
    }

    private fun initViewModels(){
        val repository = Repository()
        val mapModelFactory = MapViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, mapModelFactory)[MapViewModel::class.java]
    }

    private fun initIntents() {
        val parentDataIntent = intent.getParcelableExtra<CreateFireDataIntent>("parentData")
        parentDataIntent?.let {
            parentData = it
        }

        val userDataIntent = intent.getParcelableExtra<UserIntent>("selectedUser")
        userDataIntent?.let {
            userData = it
        }
    }

    private fun initEvents() {
        findViewById<ImageButton>(R.id.imageButtonBack).setOnClickListener {
            goBack()
        }

        findViewById<Button>(R.id.buttonConfirm).setOnClickListener {
            val isInternetAvailable = NetworkUtils.isInternetAvailable(application)
            if(isInternetAvailable) {
                coords?.let {
                    viewModel.getMapLocation(it.latitude, it.longitude, ::handleSendLocation)
                } ?: run {
                    showMessage(getString(R.string.coords_not_selected_toast))
                }
            } else {
                coords?.let {
                    val intent = Intent(this, CreateFireActivity::class.java)
                    intent.putExtra("LAT", it.latitude.toString())
                    intent.putExtra("LNG", it.longitude.toString())

                    startActivity(intent)
                    finish()
                }
            }
        }

        findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            goBack()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(portugalBounds, 0))
        googleMap.setLatLngBoundsForCameraTarget(portugalBounds)
        googleMap.setMinZoomPreference(6.0f)
        googleMap.setMaxZoomPreference(25.0f)
        googleMap.setOnMapClickListener(this)

        val sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        sharedPreferences.getBoolean("mapCached", true)
        sharedPreferences.edit().putBoolean("mapCached", true).apply()
    }

    override fun onMapClick(latLng: LatLng) {
        if (portugalBounds.contains(latLng)) {
            userMarker?.remove()
            userMarker = googleMap.addMarker(MarkerOptions().position(latLng))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            coords = latLng
        } else {
            showMessage(getString(R.string.marker_boundaries_toast))
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    private fun handleSendLocation(location: Location) {
        val zipcodeIntent = ZipcodeIntent(
            id = location.id,
            locationName = location.locationName,
            zipCode = location.zipCode,
            artName = location.artName,
            tronco = location.tronco
        )
        goBack(zipcodeIntent)
    }

    private fun goBack(zipcodeIntent: ZipcodeIntent? = null) {
        val intent = Intent(this, CreateFireActivity::class.java)
        if(zipcodeIntent != null){
            intent.putExtra("selectedZipcode", zipcodeIntent)
        }
        intent.putExtra("parentData", parentData)
        if(::userData.isInitialized) {
            intent.putExtra("selectedUser", userData)
        }
        startActivity(intent)
        finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}