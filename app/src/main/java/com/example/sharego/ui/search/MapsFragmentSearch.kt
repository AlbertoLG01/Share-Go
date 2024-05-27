package com.example.sharego.ui.search

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.example.sharego.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import android.location.Geocoder
import java.util.Locale

class MapsFragmentSearch : Fragment() {

    private var marker: Marker? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */


        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                marker = googleMap.addMarker(MarkerOptions().position(currentLocation).title("Tu Ubicación"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            }
        }

        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            marker = googleMap.addMarker(MarkerOptions().position(latLng).title("Ubicacion seleccionada"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    fun getOrigen(): GeoPoint {
        val markerPosition = marker?.position
        if (markerPosition != null) {
            return GeoPoint(markerPosition.latitude, markerPosition.longitude)
        }
        return GeoPoint(0.0, 0.0)
    }

    fun getCiudadOrigen(): String? {
        val latLng = marker?.position
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = latLng?.let { geocoder.getFromLocation(it.latitude, it.longitude, 1) }

        return if (addresses!!.isNotEmpty()) {
            addresses[0].locality // Devuelve el nombre de la ciudad
        } else {
            null // No se encontraron direcciones
        }
    }

    fun getMarker(): LatLng {
        return marker?.position ?: LatLng(0.0, 0.0)
    }
}

