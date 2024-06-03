package com.example.sharego

import com.example.sharego.dataClasses.Usuario
import com.google.firebase.firestore.DocumentReference

object UserManager {
    var usuario: Usuario? = null
    var usuarioReference: DocumentReference? = null

    fun initialize(usuario: Usuario, usuarioReference: DocumentReference) {
        this.usuario = usuario
        this.usuarioReference = usuarioReference
    }

    fun getUsuarioManager(): Usuario? {
        return usuario
    }

    fun getUsuarioReferenceManager(): DocumentReference? {
        return usuarioReference
    }
}
