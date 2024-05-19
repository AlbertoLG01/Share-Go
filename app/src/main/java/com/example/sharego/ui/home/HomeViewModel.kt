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
        value = "Estos son tus viajes:"
    }
    val text: LiveData<String> = _text

    //Base de Datos
    val db = Firebase.firestore
    val userID = "pUvzJWWI7DfpvDO0TgqP"  //ANA.... CAMBIAR POR USER ID CUANDO SE INICIE SESION

    init {
        //Obtener el nombre del usuario y sus viajes de la base de datos
        getNombreUsuario()
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

    private fun getNombreUsuario(){
        db.collection("Usuarios").document(userID).get().addOnSuccessListener {
            val nombre = it.get("nombre")
            val sexo = it.get("sexo")
            if(sexo == "Hombre")
                _text.value = "Bienvenido $nombre ! \n" + _text.value
            else if (sexo == "Mujer")
                _text.value = "Bienvenida $nombre ! \n" + _text.value
            else
                _text.value = "Bienvenid@ $nombre ! \n" + _text.value
        }
    }
}