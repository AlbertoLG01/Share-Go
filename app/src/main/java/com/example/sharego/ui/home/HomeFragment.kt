package com.example.sharego.ui.home

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharego.R
import com.example.sharego.UserManager
import com.example.sharego.dataClasses.Viaje
import com.example.sharego.databinding.FragmentHomeBinding
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var viajeAdapter: ViajeAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        viajeAdapter = ViajeAdapter(emptyList()) // Inicializa el adaptador con una lista vacía
        recyclerView.adapter = viajeAdapter

        val defaultText: TextView = binding.defaultText
        homeViewModel.viajes.observe(viewLifecycleOwner) { viajes ->
            // Actualiza la lista de viajes en el adaptador cuando cambian los datos
            viajeAdapter.updateData(viajes)

            //Oculto el texto por defecto y muestro el recyclerview
            if (viajes.isEmpty()) {
                defaultText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                defaultText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ViajeAdapter(private var viajes: List<Viaje>) : RecyclerView.Adapter<ViajeAdapter.ViajeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViajeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_home, parent, false)
        return ViajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViajeViewHolder, position: Int) {
        val viaje = viajes[position]

        // Obtener el nombre del conductor a partir de la referencia
        viaje.conductor?.get()
            ?.addOnSuccessListener { conductorDocument ->
                val nombreConductor = conductorDocument.getString("nombre")
                // Si se obtiene el nombre del conductor, actualizar el TextView correspondiente
                nombreConductor?.let {
                    holder.nombreConductorTextView.text = "Conductor: $nombreConductor"
                }
            }
            ?.addOnFailureListener { exception ->
                Log.e("ViajeAdapter", "Error al obtener el nombre del conductor: $exception")
            }

        holder.origenDestinoTextView.text = "Viaje: ${viaje.origen} -> ${viaje.destino}"
        holder.precioTextView.text = String.format("Precio: %.2f", viaje.precioPlaza)

        //Para formatear la fecha
        val fechaTimestamp = viaje.fecha.toDate() // Convertir el Timestamp a Date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) // Formato de fecha deseado
        val fechaFormateada = dateFormat.format(fechaTimestamp) // Convertir la fecha a String con el formato deseado
        holder.fechaTextView.text = "Fecha: $fechaFormateada" // Mostrar la fecha formateada en el TextView
    }

    override fun getItemCount() = viajes.size

    fun updateData(newViajes: List<Viaje>) {
        viajes = newViajes
        notifyDataSetChanged()
    }

    class ViajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreConductorTextView: TextView = itemView.findViewById(R.id.nombreConductorTextView)
        val origenDestinoTextView: TextView = itemView.findViewById(R.id.origenDestinoTextView)
        val precioTextView: TextView = itemView.findViewById(R.id.precioTextView)
        val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
    }
}
