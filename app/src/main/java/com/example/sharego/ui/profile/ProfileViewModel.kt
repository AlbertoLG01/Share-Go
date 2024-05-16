package com.example.sharego.ui.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharego.dataClasses.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<Usuario>()
    val user: LiveData<Usuario> = _user

    //Base de Datos
    val db = Firebase.firestore
    val userID = "pUvzJWWI7DfpvDO0TgqP"  //ANA.... CAMBIAR POR USER ID CUANDO SE INICIE SESION

    init {
        // Get the viajes from the database
        getUsuario()
    }

    fun getUsuario() {
        db.collection("Usuarios")
            .document(userID)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val usuario = documentSnapshot.toObject<Usuario>()
                if (usuario != null) {
                    Log.i("Usuario", "Usuario: $usuario")
                    _user.value = usuario!!
                } else {
                    Log.i("Usuario", "No se encontró ningún usuario con ese ID.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Usuario", "Error al obtener el usuario", e)
            }
    }


    fun saveUser(user: Usuario, context: Context?, callback: () -> Unit) {
        // Guardar los datos del usuario en Firestore
        db.collection("Usuarios")
            .document(userID) // Utilizar userID como ID del documento
            .set(user)
            .addOnSuccessListener {
                // Log.i("UsuarioGuardado", "Usuario guardado con éxito")
                Toast.makeText(context, "Usuario guardado correctamente", Toast.LENGTH_SHORT).show()
                // Llamar al callback una vez que se haya completado la escritura en Firestore
                callback()
            }
            .addOnFailureListener { e ->
                // Log.e("UsuarioGuardado", "Error al guardar el usuario", e)
                Toast.makeText(context, "Error al guardar el usuario", Toast.LENGTH_SHORT).show()
            }
    }
}