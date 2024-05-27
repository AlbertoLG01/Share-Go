package com.example.sharego.dataClasses

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

data class Viaje(
    var conductor: DocumentReference? = null,
    var origen: String = "",
    var destino: String = "",
    var fecha: Timestamp = Timestamp.now(),
    var plazas: Int = 0,
    var pasajeros: List<DocumentReference> = emptyList(),
    var precioPlaza: Float = 0f,
    var origenGeo: GeoPoint? = null,
    var destinoGeo: GeoPoint? = null,
    var descripcion: String = ""
)
