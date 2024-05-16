package com.example.sharego.dataClasses

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Viaje(
    val conductor: DocumentReference? = null,
    val origen: String = "",
    val destino: String = "",
    val fecha: Timestamp = Timestamp.now(),
    val plazas: Int = 0,
    val pasajeros: List<DocumentReference> = emptyList(),
    val precioPlaza: Float = 0f
)
