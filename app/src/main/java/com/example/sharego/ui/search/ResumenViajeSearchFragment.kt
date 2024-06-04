import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sharego.R
import com.example.sharego.UserManager
import com.example.sharego.dataClasses.Usuario
import com.example.sharego.dataClasses.Viaje
import com.example.sharego.ui.publish.MapsActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResumenViajeSearchFragment : Fragment() {

    private lateinit var viaje: Viaje

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as? AppCompatActivity)?.supportActionBar?.title = "Resumen del Viaje"

        // Recuperar los datos pasados en el Bundle
        val viajeId = arguments?.getString("viajeId")

        val db = Firebase.firestore
        db.collection("Viajes")
            .document(viajeId!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                viaje = documentSnapshot.toObject<Viaje>()!!
                if (viaje != null) {
                    Log.i("ResumenViaje", "Viaje: $viaje")


                    val conductorRef = viaje.conductor

                    // Consulta el documento del conductor para obtener su nombre
                    conductorRef?.get()
                        ?.addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // El documento del conductor existe, obtén su nombre
                                val conductorNombre = documentSnapshot.getString("nombre")

                                // Asigna el nombre del conductor al TextView correspondiente
                                view?.findViewById<TextView>(R.id.textViewConductor)?.text = conductorNombre ?: "Desconocido"
                            } else {
                                // El documento del conductor no existe
                                println("El documento del conductor no existe")
                                view?.findViewById<TextView>(R.id.textViewConductor)?.text = "Desconocido"
                            }
                        }
                        ?.addOnFailureListener { e ->
                            // Error al obtener el documento del conductor
                            println("Error al obtener el documento del conductor: $e")
                            view?.findViewById<TextView>(R.id.textViewConductor)?.text = "Desconocido"
                        }

                    view?.findViewById<TextView>(R.id.textViewOrigen)?.text = viaje.origen
                    view?.findViewById<TextView>(R.id.textViewDestino)?.text = viaje.destino

                    val timestamp = viaje.fecha
                    val date = Date(timestamp.seconds * 1000)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val formattedDate = dateFormat.format(date)

                    // Asigna la fecha formateada al TextView correspondiente
                    view?.findViewById<TextView>(R.id.textViewFecha)?.text = formattedDate


                    view?.findViewById<TextView>(R.id.textViewPlazas)?.text = viaje.plazas.toString()
                    view?.findViewById<TextView>(R.id.textViewPrecioPlaza)?.text =
                        viaje.precioPlaza.toString()
                    val textViewDescripcion = view?.findViewById<TextView>(R.id.textViewDescripcion)
                    textViewDescripcion?.text = viaje.descripcion

                    if(viaje.descripcion == "")
                        textViewDescripcion?.visibility = View.GONE

                    val botonResumen = view?.findViewById<Button>(R.id.btnCrearViaje)
                    botonResumen?.text = "RESERVAR VIAJE"
                    botonResumen?.setOnClickListener {

                        if(viaje.pasajeros.size == viaje.plazas){
                            val snackbar = Snackbar.make(requireView(), "El viaje está completo", Snackbar.LENGTH_LONG)
                            snackbar.show()
                        }
                        else{
                            //SI HAY PLAZA

                            // Añadir el usuario a la lista de pasajeros
                            val updatedPasajeros = viaje.pasajeros.toMutableList()
                            updatedPasajeros.add(UserManager.getUsuarioReferenceManager()!!)

                            // Actualizar el documento del viaje
                            db.collection("Viajes")
                                .document(viajeId)
                                .update("pasajeros", updatedPasajeros)
                                .addOnSuccessListener {
                                    val snackbar = Snackbar.make(requireView(), "Reservado exitosamente", Snackbar.LENGTH_LONG)
                                    snackbar.show()
                                }
                                .addOnFailureListener { e ->
                                    val snackbar = Snackbar.make(requireView(), "Error al reservar: ${e.message}", Snackbar.LENGTH_LONG)
                                    snackbar.show()
                                }
                        }

                    }
                } else {
                    Log.i("ResumenViaje", "No se encontró ningún viaje con ese ID.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("ResumenViaje", "Error al obtener el viaje", e)
            }

        return inflater.inflate(R.layout.fragment_resumen_viaje, container, false)
    }
}
