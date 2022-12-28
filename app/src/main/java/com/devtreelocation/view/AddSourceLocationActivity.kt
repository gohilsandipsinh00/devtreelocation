package com.devtreelocation.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.devtreelocation.R
import com.devtreelocation.databinding.ActivityMainBinding
import com.devtreelocation.model.SourceLocation
import com.devtreelocation.utils.FieldSelector
import com.devtreelocation.utils.KEY_IS_ID
import com.devtreelocation.utils.KEY_IS_UPDATE
import com.devtreelocation.viewModel.AddSourceLocationViewModel
import com.devtreelocation.viewModel.ViewModelFactory
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class AddSourceLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AddSourceLocationViewModel

    private lateinit var placesClient: PlacesClient
    private lateinit var fieldSelector: FieldSelector

    private lateinit var googleMap: GoogleMap

    private lateinit var sourceLocation: SourceLocation
    private var sourceLocationId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel =
            ViewModelProvider(
                this@AddSourceLocationActivity,
                ViewModelFactory(this)
            )[AddSourceLocationViewModel::class.java]

        sourceLocationId = intent.getIntExtra(KEY_IS_ID, 0)

        if (intent.getBooleanExtra(KEY_IS_UPDATE, false)) {
            binding.rlInfoView.visibility = View.VISIBLE
            binding.tvInfo.text = getString(R.string.str_info_update)
            binding.btnInfo.text = getString(R.string.str_update)
        } else {
            binding.rlInfoView.visibility = View.INVISIBLE
            binding.tvInfo.text = getString(R.string.str_info_add)
            binding.btnInfo.text = getString(R.string.str_save)
        }

        binding.btnInfo.setOnClickListener {
            try {
                if (binding.btnInfo.text == getString(R.string.str_save)) {
                    viewModel.addSourceLocation(sourceLocation)
                    finish()
                } else {
                    viewModel.updateSourceLocation(sourceLocation)
                    finish()
                }
            } catch (e: Exception) {
                finish()
            }
        }

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.GOOGLE_API_KEY))
        }

        val supportMapFragment =
            (supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)

        fieldSelector = FieldSelector(
            findViewById(R.id.custom_fields_list),
            savedInstanceState
        )

        placesClient = Places.createClient(this)

        setupAutocompleteSupportFragment()

    }

    private fun setupAutocompleteSupportFragment() {
        val autocompleteSupportFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_support_fragment) as AutocompleteSupportFragment?

        autocompleteSupportFragment?.let {
            it.setPlaceFields(placeFields)
            it.setOnPlaceSelectedListener(placeSelectionListener)
        }

    }

    private val placeSelectionListener: PlaceSelectionListener
        get() = object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                googleMap.clear()

                val latLng = place.latLng
                val markerOptions = MarkerOptions().position(latLng!!).title(place.name)
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                googleMap.addMarker(markerOptions)

                sourceLocation = SourceLocation(
                    id = sourceLocationId,
                    locationId = place.id!!,
                    locationName = place.name!!,
                    locationAddress = place.address!!,
                    locationLat = place.latLng!!.latitude,
                    locationLong = place.latLng!!.longitude
                )

                binding.rlInfoView.visibility = View.VISIBLE
            }

            override fun onError(status: Status) {
            }
        }

    private val placeFields: List<Place.Field>
        get() = fieldSelector.allFields

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            googleMap = p0
        }
    }


}