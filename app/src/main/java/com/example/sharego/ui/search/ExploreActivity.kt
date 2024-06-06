package com.example.sharego.ui.search

import ResumenViajeFragment
import ResumenViajeSearchFragment
import SelectViajeFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.sharego.MainActivity
import com.example.sharego.R
import com.example.sharego.UserManager
import com.example.sharego.dataClasses.Viaje
import com.example.sharego.databinding.ActivityMapsBinding
import com.example.sharego.ui.search.TimeRangeSelectorFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ExploreActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()
        supportActionBar?.title = "¿Desde dónde quieres salir?"

        // Configurar el SupportMapFragment dinámicamente
        val mapFragment = MapsFragmentSearch()
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedor_fragments, mapFragment, "MapsFragmentSearch")
            .commit()


        // Configurar el OnClickListener para el botón de volver atrás
        binding.fabBack.setOnClickListener {

            val currentFragment = supportFragmentManager.findFragmentById(R.id.contenedor_fragments)

            if (currentFragment is ResumenViajeSearchFragment)
                binding.fabNext.isEnabled = true

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

            if (currentFragment is MapsFragmentSearch) {
                // El fragmento actual es MapsFragment
                transaction.replace(R.id.contenedor_fragments, TimeRangeSelectorFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            } else if (currentFragment is TimeRangeSelectorFragment) {
                // El fragmento actual es TimeSelectorFragment
                //Comprobar que los minutos no distan menos de 15 (en valor abs)
                if (currentFragment.compruebaRango()){

                    val markerPosition = (supportFragmentManager.findFragmentByTag("MapsFragmentSearch") as MapsFragmentSearch).getMarker()
                    val rangoHoras = currentFragment.getRangoHoras()

                    val bundle = Bundle().apply {
                        putParcelable("markerPosition", markerPosition)
                        putSerializable("rangoHoras", rangoHoras)
                    }

                    val selectViajeFragment = SelectViajeFragment().apply {
                        arguments = bundle
                    }

                    transaction.replace(R.id.contenedor_fragments, selectViajeFragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                else{
                    val snackbar = Snackbar.make(currentFragment.requireView(), "Selecciona un rango de al menos 15 minutos de diferencia", Snackbar.LENGTH_LONG)
                    snackbar.anchorView = binding.fabNext
                    snackbar.show()
                }
            } else if (currentFragment is SelectViajeFragment){

                binding.fabNext.isEnabled = false

                val viajeId = currentFragment.getSelectedViaje()

                val bundle = Bundle().apply {
                    putString("viajeId", viajeId)
                }

                val resumenViajeSearchFragment = ResumenViajeSearchFragment().apply {
                    arguments = bundle
                }

                transaction.replace(R.id.contenedor_fragments, resumenViajeSearchFragment)
                transaction.addToBackStack(null)
                transaction.commit()

            }
            else{
                // El fragmento actual es otro fragmento
                // Realiza aquí las acciones predeterminadas o manejo de error
                Log.e("ErrorBusqueda", "Fragmento no esperado")
            }
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
