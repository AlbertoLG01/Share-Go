package com.example.sharego.ui.publish

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharego.dataClasses.Viaje

class PublishViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "¡Ahora vamos a crear un viaje! \n\n" +
                "Los pasos para crearlo son:\n\n\n" +
                "1. Selecciona la ubicación desde donde sales \n\n" +
                "2. Elige la franja horaria a la que quieres salir\n\n" +
                "3. Selecciona el viaje en el que quieres ir dentro de los que se ofrecen\n\n"

    }
    val text: LiveData<String> = _text

}