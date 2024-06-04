package com.example.sharego

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sharego.dataClasses.Usuario
import com.google.firebase.firestore.DocumentReference

object UserManager {
    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> get() = _usuario

    private val _usuarioReference = MutableLiveData<DocumentReference?>()
    val usuarioReference: LiveData<DocumentReference?> get() = _usuarioReference

    fun setUsuario(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun setUsuarioReference(reference: DocumentReference) {
        _usuarioReference.value = reference
    }

    fun getUsuarioReferenceManager(): DocumentReference? {
        return _usuarioReference.value
    }
}

