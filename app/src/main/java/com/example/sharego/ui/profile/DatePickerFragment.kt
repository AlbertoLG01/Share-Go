import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.google.firebase.Timestamp
import java.util.*

class DatePickerFragment(private var initialTimestamp: Date) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var listener: ((Date) -> Unit)? = null

    fun setListener(callback: (Date) -> Unit) {
        listener = callback
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Convertir el Timestamp a un objeto Date
        val initialDate = initialTimestamp

        // Obtener el año, mes y día de la fecha inicial
        val calendar = Calendar.getInstance()
        calendar.time = initialDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Crear y devolver un DatePickerDialog con la fecha inicial
        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
        initialTimestamp = calendar.time
        listener?.invoke(initialTimestamp)
    }
}
