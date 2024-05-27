import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sharego.R
import com.example.sharego.dataClasses.Viaje
import com.example.sharego.ui.publish.MapsActivity

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
            view.findViewById<TextView>(R.id.textViewConductor).text =
                viaje.conductor?.id ?: "Desconocido"
            view.findViewById<TextView>(R.id.textViewOrigen).text = viaje.origen
            view.findViewById<TextView>(R.id.textViewDestino).text = viaje.destino
            view.findViewById<TextView>(R.id.textViewFecha).text =
                viaje.fecha.toString() // Asegúrate de formatear la fecha según tus necesidades
            view.findViewById<TextView>(R.id.textViewPlazas).text = viaje.plazas.toString()
            view.findViewById<TextView>(R.id.textViewPrecioPlaza).text =
                viaje.precioPlaza.toString()
            view.findViewById<TextView>(R.id.textViewDescripcion).text = viaje.descripcion
        }

    }
}
