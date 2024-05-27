package com.example.sharego.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "¡Bienvenido a la búsqueda de viajes! \n\n" +
                "Ten en cuenta los siguientes aspectos para buscar un viaje:\n\n\n" +
                "1. Selecciona la ubicación desde donde quieres salir \n\n" +
                "2. Elige la franja horaria a la que quieres salir\n\n" +
                "3. Selecciona el viaje en el que quieres ir dentro de los que se ofrecen\n\n"
    }
    val text: LiveData<String> = _text
}