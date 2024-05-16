package com.example.sharego.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharego.dataClasses.Viaje
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.toObjects

class HomeViewModel : ViewModel() {

    private val _viajes = MutableLiveData<List<Viaje>>()
    val viajes: LiveData<List<Viaje>> = _viajes

    private val _text = MutableLiveData<String>().apply {
        value = "Tus Viajes"
    }
    val text: LiveData<String> = _text

    //Base de Datos
    val db = Firebase.firestore
    val userID = "pUvzJWWI7DfpvDO0TgqP"  //ANA.... CAMBIAR POR USER ID CUANDO SE INICIE SESION

    init {
        // Get the viajes from the database
        getViajes()
    }

    private fun getViajes() {

        db.collection("Viajes")
            .whereArrayContains("pasajeros", db.document("Usuarios/$userID"))
            .get()
            .addOnSuccessListener { querySnapshot ->
                val viajeList = querySnapshot.toObjects<Viaje>()
                Log.i("viajeList", "viajeList: $viajeList")
                _viajes.value = viajeList
            }

    }
}