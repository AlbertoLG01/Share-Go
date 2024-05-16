package com.example.sharego.dataClasses

import com.google.firebase.Timestamp

data class Usuario(
    val apellido1: String = "",
    val apellido2: String = "",
    val nombre: String = "",
    val email: String = "",
    val sexo: String = "",
    val telefono: Int = 0,
    val fechaNacimiento: Timestamp = Timestamp.now()
)
