package com.example.sharego.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.sharego.UserManager
import com.example.sharego.dataClasses.Viaje
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.toObjects

class HomeViewModel : ViewModel() {

    private val _viajes = MutableLiveData<List<Viaje>>()
    val viajes: LiveData<List<Viaje>> = _viajes

    private val _text = MutableLiveData<String>().apply {
        value = "Estos son tus viajes:"
    }
    val text: LiveData<String> = _text

    // Base de Datos
    val db = Firebase.firestore

    init {
        // Observar los cambios en el usuario
        UserManager.usuarioReference.observeForever { usuarioReference ->
            if (usuarioReference != null) {
                getViajes(usuarioReference.id)
                getNombreUsuario(usuarioReference.id)
            }
        }
    }

    private fun getViajes(userID: String) {
        db.collection("Viajes")
            .whereArrayContains("pasajeros", db.document("Usuarios/$userID"))
            .get()
            .addOnSuccessListener { querySnapshot ->
                val viajeList = querySnapshot.toObjects<Viaje>()
                Log.i("viajeList", "viajeList: $viajeList")
                _viajes.value = viajeList
            }
    }

    private fun getNombreUsuario(userID: String) {
        db.collection("Usuarios").document(userID).get().addOnSuccessListener {
            val nombre = it.getString("nombre")
            val sexo = it.getString("sexo")
            _text.value = when (sexo) {
                "Hombre" -> "Bienvenido $nombre ! \n${_text.value}"
                "Mujer" -> "Bienvenida $nombre ! \n${_text.value}"
                else -> "Bienvenid@ $nombre ! \n${_text.value}"
            }
        }
    }
}
