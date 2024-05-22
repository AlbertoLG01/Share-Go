package com.example.sharego.ui.publish

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sharego.MainActivity
import com.example.sharego.R
import com.example.sharego.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()
        supportActionBar?.title = "¿Desde dónde sales?"

        // Configurar el SupportMapFragment dinámicamente
        val mapFragment = MapsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedor_fragments, mapFragment)
            .commit()


        // Configurar el OnClickListener para el botón de volver atrás
        binding.fabBack.setOnClickListener {
            onBackPressed() // Esta función invoca el comportamiento predeterminado de "volver atrás"
        }

        // Configurar el OnClickListener para el botón de cancelar
        binding.fabCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Flags para borrar la pila de actividades anteriores
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // Configurar el OnClickListener para el botón de continuar
        binding.fabNext.setOnClickListener {

            val currentFragment = supportFragmentManager.findFragmentById(R.id.contenedor_fragments)


            //Navegar al siguiente fragmento
            val transaction = supportFragmentManager.beginTransaction()

            if (currentFragment is MapsFragment) {
                // El fragmento actual es MapsFragment
                transaction.replace(R.id.contenedor_fragments, TimeSelectorFragment())
            } else if (currentFragment is TimeSelectorFragment) {
                // El fragmento actual es TimeSelectorFragment
                TODO()
                //Comprobar que los minutos no distan menos de 15 (en valor abs)
                //transaction.replace(R.id.contenedor_fragments, MapsFragment2())
            } else {
                // El fragmento actual es otro fragmento
                // Realiza aquí las acciones predeterminadas o manejo de error
            }



            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            mMap.isMyLocationEnabled = true
//
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                if (location != null) {
//                    val currentLocation = LatLng(location.latitude, location.longitude)
//                    mMap.addMarker(MarkerOptions().position(currentLocation).title("Tu Ubicación"))
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
//                }
//            }
//        }
//
//        mMap.setOnMapClickListener { latLng ->
//            mMap.clear()
//            mMap.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//        }
//    }
//
//    private fun requestLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
//    }
//
//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
//    }

    fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }
}
