package com.example.sharego.ui.publish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sharego.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class TimeSelectorFragment : Fragment() {

    private lateinit var selectedDateTimeTextView: TextView
    private lateinit var startDateEditText: EditText
    private lateinit var startTimeEditText: EditText

    private var selectedDate: Long? = null
    private var selectedTime: Pair<Int, Int>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_selector, container, false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "¿Cuando sales?"

        selectedDateTimeTextView = view.findViewById(R.id.selectedDateTime)
        startDateEditText = view.findViewById(R.id.startDateEditText)
        startTimeEditText = view.findViewById(R.id.startTimeEditText)

        startDateEditText.setOnClickListener { showDatePicker() }
        startTimeEditText.setOnClickListener { showTimePicker() }

        return view
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona la fecha del viaje")
            .build()

        datePicker.addOnPositiveButtonClickListener { epochMillis ->
            selectedDate = epochMillis
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(epochMillis))
            startDateEditText.setText(formattedDate)
            updateSelectedDateTime()
        }

        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY) % 12)
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText("Selecciona la hora del viaje")
            .build()

        picker.addOnPositiveButtonClickListener {
            selectedTime = Pair(picker.hour, picker.minute)
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                picker.hour, picker.minute, if (picker.hour < 12) "AM" else "PM")
            startTimeEditText.setText(formattedTime)
            updateSelectedDateTime()
        }

        picker.show(childFragmentManager, "TIME_PICKER")
    }

    private fun updateSelectedDateTime() {
        if (selectedDate != null && selectedTime != null) {
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate!!))
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                selectedTime!!.first, selectedTime!!.second, if (selectedTime!!.first < 12) "AM" else "PM")
            val dateTimeString = "Fecha y hora seleccionadas: $formattedDate $formattedTime"
            selectedDateTimeTextView.text = dateTimeString
        }
    }

    fun compruebaFechaYHora(): Boolean {
        return (selectedDate != null && selectedTime != null)
    }

    fun getFecha(): Timestamp {
        val calendar = Calendar.getInstance().apply {
            clear()
            if (selectedDate != null) {
                timeInMillis = selectedDate!!
            }
            if (selectedTime != null) {
                val (hour, minute) = selectedTime!!
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
        }
        return Timestamp(calendar.time)
    }

}
