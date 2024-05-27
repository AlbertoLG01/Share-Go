import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sharego.R
import com.example.sharego.dataClasses.Viaje
import com.example.sharego.ui.publish.MapsActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResumenViajeFragment : Fragment() {

    private lateinit var viaje: Viaje

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as? AppCompatActivity)?.supportActionBar?.title = "Resumen del Viaje"

        viaje = (activity as MapsActivity).getViaje()

        return inflater.inflate(R.layout.fragment_resumen_viaje, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Asignar valores del objeto Viaje a TextViews

        if (viaje != null) {

            val db = Firebase.firestore

            val conductorRef = viaje.conductor

            // Consulta el documento del conductor para obtener su nombre
            conductorRef?.get()
                ?.addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // El documento del conductor existe, obtén su nombre
                        val conductorNombre = documentSnapshot.getString("nombre")

                        // Asigna el nombre del conductor al TextView correspondiente
                        view.findViewById<TextView>(R.id.textViewConductor).text = conductorNombre ?: "Desconocido"
                    } else {
                        // El documento del conductor no existe
                        println("El documento del conductor no existe")
                        view.findViewById<TextView>(R.id.textViewConductor).text = "Desconocido"
                    }
                }
                ?.addOnFailureListener { e ->
                    // Error al obtener el documento del conductor
                    println("Error al obtener el documento del conductor: $e")
                    view.findViewById<TextView>(R.id.textViewConductor).text = "Desconocido"
                }

            view.findViewById<TextView>(R.id.textViewOrigen).text = viaje.origen
            view.findViewById<TextView>(R.id.textViewDestino).text = viaje.destino

            val timestamp = viaje.fecha
            val date = Date(timestamp.seconds * 1000)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(date)

            // Asigna la fecha formateada al TextView correspondiente
            view.findViewById<TextView>(R.id.textViewFecha).text = formattedDate


            view.findViewById<TextView>(R.id.textViewPlazas).text = viaje.plazas.toString()
            view.findViewById<TextView>(R.id.textViewPrecioPlaza).text =
                viaje.precioPlaza.toString()
            val textViewDescripcion = view.findViewById<TextView>(R.id.textViewDescripcion)
            textViewDescripcion.text = viaje.descripcion

            if(viaje.descripcion == "")
                textViewDescripcion.visibility = View.GONE

            view.findViewById<Button>(R.id.btnCrearViaje).setOnClickListener {

                // Obtén una referencia a la colección "Viajes"
                val viajesCollection = db.collection("Viajes")

                // Crea un nuevo documento en la colección "Viajes" con los datos del viaje
                viajesCollection
                    .add(viaje)
                    .addOnSuccessListener { documentReference ->
                        // Éxito: El viaje se ha insertado correctamente
                        println("Viaje insertado correctamente con ID: ${documentReference.id}")
                        val snackbar = Snackbar.make(requireView(), "Viaje creado correctamente", Snackbar.LENGTH_LONG)
                        snackbar.show()

                        // Cierra la actividad en la que se encuentra este fragmento
                        activity?.finish()
                    }
                    .addOnFailureListener { e ->
                        // Error: No se pudo insertar el viaje
                        println("Error al insertar el viaje: $e")
                    }
            }

        }

    }
}
