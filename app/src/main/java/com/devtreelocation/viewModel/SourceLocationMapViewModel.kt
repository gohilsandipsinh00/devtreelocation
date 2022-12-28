package com.devtreelocation.viewModel

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtreelocation.model.MapData
import com.devtreelocation.model.SourceLocation
import com.devtreelocation.repository.SourceLocationRepository
import com.devtreelocation.roomdb.SourceLocationDatabase
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class SourceLocationMapViewModel(context: Context) : ViewModel() {

    var readAllData: LiveData<List<SourceLocation>>

    var url: MutableLiveData<String> = MutableLiveData()

    private val repository: SourceLocationRepository

    var isSort = 0

    init {
        val userDao = SourceLocationDatabase.getDatabase(context).sourceLocationDao()
        repository = SourceLocationRepository(userDao)
        readAllData = repository.readAllData()
    }

    fun getRoutePolyline(secret: String) {

        val list = readAllData.value

        list?.let {

            if (isSort == 1) {
                list.sortedWith(
                    compareBy(
                        SourceLocation::locationName
                    )
                )
            } else if (isSort == 2) {
                list.sortedWith(
                    compareByDescending(
                        SourceLocation::locationName
                    )
                )
            }

            var origin = ""
            var waypoints = ""
            var destination = ""

            for (i in 0 until it.size) {

                if (i == 0) {
                    origin = "origin=" + it[i].locationLat + "," + it[i].locationLong
                } else if (i == (it.size - 1) && it.size != 2) {
                    if (waypoints.isEmpty()) {
                        waypoints =
                            "waypoint=optimize:true|" + it[i].locationLat + "," + it[i].locationLong + "|"
                    } else {
                        waypoints =
                            waypoints + it[i].locationLat + "," + it[i].locationLong + "|"
                    }
                } else {
                    destination = "destination=" + it[i].locationLat + "," + it[i].locationLong
                }
            }

            val sensor = "sensor=false"
            val params = "$origin&$waypoints&$destination&$sensor&mode=driving&key=$secret"
            val output = "json"
            url.value = "https://maps.googleapis.com/maps/api/directions/$output?$params"
        }

    }

    fun getDirection(url: String, googleMap: GoogleMap) {

        viewModelScope.launch(Dispatchers.Main) {
            val result = ArrayList<List<LatLng>>()

            withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val data = response.body!!.string()

                try {
                    val respObj = Gson().fromJson(data, MapData::class.java)
                    val path = ArrayList<LatLng>()
                    for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                        path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                    }
                    result.add(path)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val lineoption = PolylineOptions()

            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(15f)
                lineoption.color(Color.RED)
                lineoption.geodesic(true)
            }

            googleMap.addPolyline(lineoption)

        }

    }


    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }


}