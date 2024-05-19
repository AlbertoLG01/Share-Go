package com.example.sharego.ui.profile

import DatePickerFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sharego.dataClasses.Usuario
import com.example.sharego.databinding.FragmentProfileBinding
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {
    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe changes in the ViewModel and update the UI accordingly.
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apellido1EditText.setText(user.apellido1)
            binding.apellido2EditText.setText(user.apellido2)
            binding.nombreEditText.setText(user.nombre)
            binding.emailEditText.setText(user.email)
            binding.sexoEditText.setText(user.sexo)
            binding.telefonoEditText.setText(user.telefono.toString())

            //Fecha formateada
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(user.fechaNacimiento.toDate())
            binding.fechaNacEditText.setText(formattedDate)

        }

        binding.fechaNacEditText.setOnClickListener {
            //Fecha formateada
            val dateFormat2 = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val formattedDate2 = dateFormat2.format(viewModel.user.value!!.fechaNacimiento.toDate())
            showDatePicker(Date(formattedDate2))
        }

        // Set up event listeners for UI elements, such as buttons or text fields.
        binding.guardarButton.setOnClickListener {

            // Convertir el texto del EditText a un objeto Date
            val fechaNac = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(binding.fechaNacEditText.text.toString())

            // Save user profile changes.
            val user = Usuario(
                apellido1 = binding.apellido1EditText.text.toString(),
                apellido2 = binding.apellido2EditText.text.toString(),
                nombre = binding.nombreEditText.text.toString(),
                email = binding.emailEditText.text.toString(),
                sexo = binding.sexoEditText.text.toString(),
                telefono = binding.telefonoEditText.text.toString().toInt(),
                fechaNacimiento = Timestamp(fechaNac!!)
            )
            viewModel.saveUser(user, context){
                viewModel.getUsuario()
            }
        }

    }
    private fun showDatePicker(fechaInicial: Date) {
        //Log.d("ProfileFragment", "Fecha antes: $fechaInicial")

        val datePickerFragment = DatePickerFragment(fechaInicial)
        datePickerFragment.show(childFragmentManager, "datePicker")
        datePickerFragment.setListener { fecha ->
            // Formatear la fecha seleccionada y mostrarla en el EditText
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha)
            binding.fechaNacEditText.setText(formattedDate)
            //Log.d("ProfileFragment", "Fecha despues: $fecha")
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}