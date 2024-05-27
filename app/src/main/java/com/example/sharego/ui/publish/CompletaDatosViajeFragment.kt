package com.example.sharego.ui.publish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sharego.R
import com.google.android.material.textfield.TextInputEditText

class CompletaDatosViajeFragment : Fragment() {

    private lateinit var autoCompleteNumPlazas: AutoCompleteTextView
    private lateinit var editTextPrecioPlaza: TextInputEditText
    private lateinit var editTextDescripcion: TextInputEditText

    private var numPlazas: Int = 0
    private var precioPlaza: Float = 0f
    private var descripcion: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_completa_datos_viaje, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "¿Cuantos pasajeros?"

        // Initialize views
        autoCompleteNumPlazas = view.findViewById(R.id.spinnerNumPlazas)
        editTextPrecioPlaza = view.findViewById(R.id.editTextPrecioPlaza)
        editTextDescripcion = view.findViewById(R.id.editTextDescripcion)

        // Set up AutoCompleteTextView for number of seats
        val numPlazasItems = resources.getStringArray(R.array.num_plazas_array)
        val numPlazasAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, numPlazasItems)
        autoCompleteNumPlazas.setAdapter(numPlazasAdapter)


        return view
    }

    fun validateInputs(): Boolean {

        numPlazas = autoCompleteNumPlazas.text.toString().toInt()
        precioPlaza = editTextPrecioPlaza.text.toString().toFloat()
        descripcion = editTextDescripcion.text.toString()

        var isValid = true

        if (autoCompleteNumPlazas.text.isNullOrEmpty()) {
            autoCompleteNumPlazas.error = "Selecciona el número de plazas"
            isValid = false
        }

        if (editTextPrecioPlaza.text.isNullOrEmpty()) {
            editTextPrecioPlaza.error = "Introduce el precio por plaza"
            isValid = false
        } else {
            try {
                editTextPrecioPlaza.text.toString().toDouble()
            } catch (e: NumberFormatException) {
                editTextPrecioPlaza.error = "El precio debe ser un número"
                isValid = false
            }
        }

        return isValid
    }

    fun getNumplazas(): Int {
        return numPlazas
    }

    fun getPrecioPlaza(): Float {
        return precioPlaza
    }

    fun getDescripcion(): String {
        return descripcion
    }
}
