package com.example.sharego.dataClasses

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Viaje(
    val Conductor: DocumentReference? = null,
    val Origen: String = "",
    val Destino: String = "",
    val Fecha: Timestamp = Timestamp.now(),
    val Plazas: Int = 0,
    val Pasajeros: List<DocumentReference> = emptyList(),
    val PrecioPlaza: Float = 0f
)
