import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.*

class SelectViajeFragment : Fragment() {

    private var markerPosition: LatLng? = null
    private var rangoHoras: Pair<Pair<Int, Int>, Pair<Int, Int>>? = null
    private var markerSeleccionado: Marker? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

        // Recuperar los datos pasados en el Bundle
        markerPosition = arguments?.getParcelable("markerPosition")
        rangoHoras = arguments?.getSerializable("rangoHoras") as? Pair<Pair<Int, Int>, Pair<Int, Int>>

    }

    private val callback = OnMapReadyCallback { map ->
        googleMap = map

        // Coloca el marcador en el mapa usando markerPosition
        markerPosition?.let {

            val markerOptions = MarkerOptions().position(markerPosition!!).title("Ubicación seleccionada")

            // Cambiar el color del marcador
            val color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            markerOptions.icon(color)

            googleMap.addMarker(markerOptions)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }

        // Desactivar clic en el mapa
        googleMap.setOnMapClickListener(null)

        // Configurar clic en los marcadores
        googleMap.setOnMarkerClickListener { marker ->
            markerSeleccionado = marker
            marker.showInfoWindow()
            true
        }

        // Utiliza los datos aquí según sea necesario
        if (rangoHoras != null) {
            fetchAndDisplayViajes(rangoHoras!!)
        }

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

    private fun fetchAndDisplayViajes(rangoHoras: Pair<Pair<Int, Int>, Pair<Int, Int>>) {
        val db = FirebaseFirestore.getInstance()

        db.collection("Viajes").get()
            .addOnSuccessListener { result ->
                var firstMarkerAdded = false
                for (document in result) {
                    val fecha = document.getTimestamp("fecha")
                    if (fecha != null) {
                        val calendar = Calendar.getInstance()
                        calendar.time = fecha.toDate()
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val minute = calendar.get(Calendar.MINUTE)

                        val startHour = rangoHoras.first.first
                        val startMinute = rangoHoras.first.second
                        val endHour = rangoHoras.second.first
                        val endMinute = rangoHoras.second.second

                        if (isWithinTimeRange(hour, minute, startHour, startMinute, endHour, endMinute)) {
                            val geoPoint = document.getGeoPoint("origenGeo")
                            if (geoPoint != null) {
                                val viajePosition = LatLng(geoPoint.latitude, geoPoint.longitude)
                                if (isWithinRadius(markerPosition!!, viajePosition, 3000.0)) {
                                    val marker = googleMap.addMarker(
                                        MarkerOptions()
                                            .position(viajePosition)
                                            .title("Viaje encontrado")
                                    )

                                    marker?.tag = document.id

                                    if (!firstMarkerAdded) {
                                        markerSeleccionado = marker
                                        marker?.showInfoWindow()
                                        firstMarkerAdded = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
            }
    }

    private fun isWithinTimeRange(
        hour: Int, minute: Int,
        startHour: Int, startMinute: Int,
        endHour: Int, endMinute: Int
    ): Boolean {
        val startTime = startHour * 60 + startMinute
        val endTime = endHour * 60 + endMinute
        val currentTime = hour * 60 + minute

        return if (startTime <= endTime) {
            currentTime in startTime..endTime
        } else {
            currentTime >= startTime || currentTime <= endTime
        }
    }

    private fun isWithinRadius(center: LatLng, point: LatLng, radius: Double): Boolean {
        val results = FloatArray(1)
        Location.distanceBetween(
            center.latitude, center.longitude,
            point.latitude, point.longitude,
            results
        )
        return results[0] <= radius
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    fun getSelectedViaje() : String{
        return markerSeleccionado?.tag.toString()
    }

}
