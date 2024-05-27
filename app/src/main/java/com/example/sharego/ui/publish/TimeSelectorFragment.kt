package com.example.sharego.ui.publish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sharego.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.math.abs

class TimeSelectorFragment : Fragment() {

    private lateinit var selectedTimeRangeTextView: TextView
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText
    private var startHour: Int = 0
    private var startMinute: Int = 0
    private var endHour: Int = 0
    private var endMinute: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_selector, container, false)
        selectedTimeRangeTextView = view.findViewById(R.id.selectedTimeRange)
        startTimeEditText = view.findViewById(R.id.startTimeEditText)
        endTimeEditText = view.findViewById(R.id.endTimeEditText)

        startTimeEditText.setOnClickListener { showStartTimePicker() }
        endTimeEditText.setOnClickListener { showEndTimePicker() }

        return view
    }

    private fun showStartTimePicker() {
        val calendar = Calendar.getInstance()
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY) % 12)
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText("Selecciona la hora de inicio")
            .build()

        picker.addOnPositiveButtonClickListener {
            startHour = picker.hour
            startMinute = picker.minute
            val startPeriod = if (picker.hour < 12) "AM" else "PM"
            startTimeEditText.setText(String.format(Locale.getDefault(), "%02d:%02d %s", startHour, startMinute, startPeriod))
            updateSelectedTimeRange()
        }

        picker.addOnDismissListener {
            val activity = requireActivity() as MapsActivity
            activity.hideSystemUI()
        }

        picker.show(childFragmentManager, "START_TIME_PICKER")
    }

    private fun showEndTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(startHour)
            .setMinute(startMinute)
            .setTitleText("Selecciona la hora de fin")
            .build()

        picker.addOnPositiveButtonClickListener {
            endHour = picker.hour
            endMinute = picker.minute
            val endPeriod = if (picker.hour < 12) "AM" else "PM"
            endTimeEditText.setText(String.format(Locale.getDefault(), "%02d:%02d %s", endHour, endMinute, endPeriod))
            updateSelectedTimeRange()
        }

        picker.addOnDismissListener {
            val activity = requireActivity() as MapsActivity
            activity.hideSystemUI()
        }

        picker.show(childFragmentManager, "END_TIME_PICKER")
    }

    private fun updateSelectedTimeRange() {
        val startPeriod = if (startHour < 12) "AM" else "PM"
        val endPeriod = if (endHour < 12) "AM" else "PM"
        val startTime = String.format(Locale.getDefault(), "%02d:%02d %s", startHour, startMinute, startPeriod)
        val endTime = String.format(Locale.getDefault(), "%02d:%02d %s", endHour, endMinute, endPeriod)
        val timeRange = "Franja horaria seleccionada: $startTime - $endTime"
        selectedTimeRangeTextView.text = timeRange
    }

    fun compruebaRango(): Boolean {
        return (abs(endMinute-startMinute) >= 15)
    }
}
