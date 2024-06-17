package com.example.sharego.ui.publish

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.sharego.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.firestore.GeoPoint
import java.util.Locale


class MapsFragment : Fragment() {

    private var marker: Marker? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        if (marker == null) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    marker = googleMap?.addMarker(MarkerOptions().position(currentLocation).title("Tu Ubicación"))
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                }
            }

            googleMap?.setOnMapClickListener { latLng ->
                googleMap?.clear()
                marker = googleMap?.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))
                googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Places
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
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
        Log.d("MapsFragment", "onViewCreated called")

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        requestLocationPermission()

        Places.initialize(requireContext(), R.string.google_maps_key.toString())

        val placesClient = Places.createClient(requireContext())

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        Log.d("MapsFragment", "AutocompleteSupportFragment initialized")

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        Log.d("MapsFragment", "Place fields set")

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("MapsFragment", "Place selected: ${place.name}, ${place.latLng}")
                googleMap?.clear()
                marker = googleMap?.addMarker(MarkerOptions().position(place.latLng!!).title(place.name))
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 15f))
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.e("MapsFragment", "An error occurred: $status")
            }
        })
        Log.d("MapsFragment", "PlaceSelectionListener set")
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
        val addresses = latLng?.let { geocoder.getFromLocation(it.latitude, latLng.longitude, 1) }

        return if (addresses!!.isNotEmpty()) {
            addresses[0].locality // Devuelve el nombre de la ciudad
        } else {
            null // No se encontraron direcciones
        }
    }

    fun getMarker(): LatLng? {
        return marker?.position
    }
}
