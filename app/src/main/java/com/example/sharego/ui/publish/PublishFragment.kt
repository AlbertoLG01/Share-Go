package com.example.sharego.ui.publish

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.sharego.MainActivity
import com.example.sharego.R
import com.example.sharego.dataClasses.Viaje
import com.example.sharego.databinding.FragmentPublishBinding
import com.example.sharego.ui.profile.ProfileViewModel
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.model.Document
import com.google.type.LatLng

class PublishFragment : Fragment(), OnLocationSelectedListener {

    private var _binding: FragmentPublishBinding? = null
    private val viewModel: PublishViewModel by viewModels()

    private var viaje = Viaje()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPublishBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPublish
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        navigateToMapsFragment()

        val mainActivity = requireActivity() as MainActivity
        viaje.conductor = mainActivity.getUsuarioReference()
        return root
    }

    override fun onLocationSelected(location: com.google.android.gms.maps.model.LatLng) {
        // Aquí actualiza el objeto Viaje según corresponda

        if(viaje.origenGeo == null){
            viaje.origenGeo = GeoPoint(location.latitude, location.longitude)
            Log.d("PublishFragment", "Añadiendo origen... ${viaje.origenGeo}")
            viewModel.paso2()
            navigateToMapsFragment()
        }
        else{
            viaje.destinoGeo = GeoPoint(location.latitude, location.longitude)
            Log.d("PublishFragment", "Añadiendo destino... ${viaje.destinoGeo}")
        }

    }

    fun navigateToMapsFragment() {
        // Obtén el FragmentManager
        val fragmentManager = childFragmentManager

        // Crea una instancia del fragmento del mapa
        val mapsFragment = MapsFragment()

        // Inicia una transacción de fragmentos
        val transaction = fragmentManager.beginTransaction()

        // Reemplaza el contenido actual con el fragmento del mapa
        transaction.replace(R.id.fragment_container, mapsFragment)

        // Añade la transacción al back stack para permitir la navegación hacia atrás si es necesario
        transaction.addToBackStack(null)

        // Realiza la transacción
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}