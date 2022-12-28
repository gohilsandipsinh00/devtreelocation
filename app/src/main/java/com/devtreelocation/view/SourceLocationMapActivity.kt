package com.devtreelocation.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.devtreelocation.R
import com.devtreelocation.databinding.ActivitySourceLocationMapBinding
import com.devtreelocation.utils.KEY_IS_SHORT
import com.devtreelocation.viewModel.SourceLocationMapViewModel
import com.devtreelocation.viewModel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SourceLocationMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivitySourceLocationMapBinding
    private lateinit var viewModel: SourceLocationMapViewModel

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySourceLocationMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel =
            ViewModelProvider(
                this@SourceLocationMapActivity,
                ViewModelFactory(this)
            )[SourceLocationMapViewModel::class.java]

        val supportMapFragment =
            (supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)

        setObservers()
    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            googleMap = p0
        }
    }

    private fun setObservers() {
        viewModel.readAllData.observe(this) {

            if (it.isNotEmpty()) {
                viewModel.isSort = intent.getIntExtra(KEY_IS_SHORT, 0)

                for (data in it) {
                    val originLocation = LatLng(data.locationLat, data.locationLong)
                    googleMap.addMarker(MarkerOptions().position(originLocation))
                }

                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it[0].locationLat,
                            it[0].locationLong
                        ), 5F
                    )
                )

                viewModel.getRoutePolyline(getString(R.string.GOOGLE_API_KEY))
            }


        }

        viewModel.url.observe(this) {
            viewModel.getDirection(it, googleMap)
        }
    }

}