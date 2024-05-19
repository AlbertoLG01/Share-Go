package com.example.sharego.ui.publish

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharego.dataClasses.Viaje

class PublishViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "1. Selecciona desde donde vas a salir"
    }
    val text: LiveData<String> = _text


    fun paso2(){
        _text.value = "2. Selecciona dónde va a ser tu destino"
    }
}