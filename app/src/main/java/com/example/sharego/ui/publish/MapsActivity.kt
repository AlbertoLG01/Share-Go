package com.example.sharego.ui.publish

import ResumenViajeFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sharego.MainActivity
import com.example.sharego.R
import com.example.sharego.UserManager
import com.example.sharego.dataClasses.Viaje
import com.example.sharego.databinding.ActivityMapsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MapsActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMapsBinding
    private lateinit var viaje: Viaje

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()
        supportActionBar?.title = "¿Desde dónde sales?"

        viaje = Viaje()

        // Configurar el SupportMapFragment dinámicamente
        val mapFragment = MapsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedor_fragments, mapFragment)
            .commit()


        // Configurar el OnClickListener para el botón de volver atrás
        binding.fabBack.setOnClickListener {

            val currentFragment = supportFragmentManager.findFragmentById(R.id.contenedor_fragments)

            if (currentFragment is ResumenViajeFragment)
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

            if (currentFragment is MapsFragment) {
                // El fragmento actual es MapsFragment

                viaje.origenGeo = currentFragment.getOrigen()
                viaje.origen = currentFragment.getCiudadOrigen()!!

                transaction.replace(R.id.contenedor_fragments, MapsFragment2())
                transaction.addToBackStack(null)
                transaction.commit()
            } else if (currentFragment is MapsFragment2) {
                // El fragmento actual es MapsFragment

                viaje.destinoGeo = currentFragment.getDestino()
                viaje.destino = currentFragment.getCiudadDestino()!!

                transaction.replace(R.id.contenedor_fragments, TimeSelectorFragment())
                transaction.addToBackStack(null)
                transaction.commit()

            } else if (currentFragment is TimeSelectorFragment) {
                // El fragmento actual es TimeSelectorFragment
                //Comprobar que los minutos no distan menos de 15 (en valor abs)
                if (currentFragment.compruebaFechaYHora()){

                    viaje.fecha = currentFragment.getFecha()

                    transaction.replace(R.id.contenedor_fragments, CompletaDatosViajeFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                else{
                    val snackbar = Snackbar.make(currentFragment.requireView(), "Selecciona una hora por favor", Snackbar.LENGTH_LONG)
                    snackbar.anchorView = binding.fabNext
                    snackbar.show()
                }
            } else if (currentFragment is CompletaDatosViajeFragment){
                if(currentFragment.validateInputs()){

                    viaje.plazas = currentFragment.getNumplazas()
                    viaje.precioPlaza = currentFragment.getPrecioPlaza()
                    viaje.descripcion = currentFragment.getDescripcion()

                    val db = Firebase.firestore
                    viaje.conductor = db.collection("Usuarios").document(UserManager.getUsuarioReferenceManager()?.id!!)

                    binding.fabNext.isEnabled = false

                    transaction.replace(R.id.contenedor_fragments, ResumenViajeFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
            else{
                // El fragmento actual es otro fragmento
                // Realiza aquí las acciones predeterminadas o manejo de error
                Log.e("MapsActivity", "Fallo en el manejo de fragmentos")
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

    fun getViaje() : Viaje{
        return viaje
    }
}
