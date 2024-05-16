package com.example.sharego.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sharego.dataClasses.Usuario
import com.example.sharego.databinding.FragmentProfileBinding
import com.google.firebase.Timestamp
import java.util.Date

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

        // Set up event listeners for UI elements, such as buttons or text fields.
        binding.guardarButton.setOnClickListener {
            // Save user profile changes.
            val user = Usuario(
                apellido1 = binding.apellido1EditText.text.toString(),
                apellido2 = binding.apellido2EditText.text.toString(),
                nombre = binding.nombreEditText.text.toString(),
                email = binding.emailEditText.text.toString(),
                sexo = binding.sexoEditText.text.toString(),
                telefono = binding.telefonoEditText.text.toString().toInt(),
                fechaNacimiento = Timestamp(Date(binding.datePicker.year, binding.datePicker.month, binding.datePicker.dayOfMonth))
            )
            viewModel.saveUser(user, context){
                viewModel.getUsuario()
            }
        }

        // Observe changes in the ViewModel and update the UI accordingly.
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apellido1EditText.setText(user.apellido1)
            binding.apellido2EditText.setText(user.apellido2)
            binding.nombreEditText.setText(user.nombre)
            binding.emailEditText.setText(user.email)
            binding.sexoEditText.setText(user.sexo)
            binding.telefonoEditText.setText(user.telefono.toString())
            binding.datePicker.updateDate(user.fechaNacimiento.toDate().year, user.fechaNacimiento.toDate().month, user.fechaNacimiento.toDate().day)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}