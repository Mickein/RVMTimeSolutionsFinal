package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import za.co.varsitycollege.st10215473.rvmtimesolutions.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalendarFragment : Fragment() {
    private lateinit var calendar: CalendarView
    private lateinit var selectedDate: Date
    private lateinit var linear: LinearLayout
    private lateinit var addTimeButton: ImageButton
    private lateinit var eventName: EditText
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendar = view.findViewById(R.id.calendarView)
        linear = view.findViewById(R.id.linearLayout)


        calendar.setOnDateChangeListener { _, year, month, dayOfMonth->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
            showAddEventDialog()
        }
        return view
    }

    private fun showAddEventDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val eventForm = layoutInflater.inflate(R.layout.add_event, null)
        dialogBuilder.setView(eventForm)
        val dialog = dialogBuilder.create()
        dialog.show()

        addTimeButton = eventForm.findViewById(R.id.btnAddTime)
        eventName = eventForm.findViewById(R.id.edtEventName)

        addTimeButton.setOnClickListener {
            dialog.hide()
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)
            val materialTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText("Select Time")
                .setHour(hour)
                .setMinute(minute)
                .build()
            materialTimePicker.addOnPositiveButtonClickListener {
                val eventN = eventName.text.toString()
                addEvent(materialTimePicker.hour, materialTimePicker.minute, eventN)
            }
            materialTimePicker.show(parentFragmentManager, "material_time_picker")
        }
    }

    fun addEvent(hour: Int, minute: Int, name: String) {
        val modifiedHour = getHourAmPm(hour)
        val amPm = if (hour > 11) "PM" else "AM"
        val numberFormat = DecimalFormat("00")


        val cardView = layoutInflater.inflate(R.layout.event_details, null)
        val eventNameTextView = cardView.findViewById<TextView>(R.id.exame_name_txt)
        val eventDateTextView = cardView.findViewById<TextView>(R.id.event_date_txt)
        val eventTimeTextView = cardView.findViewById<TextView>(R.id.event_time_txt)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = sdf.format(selectedDate)

        eventNameTextView.text = "Event Name: $name"

        // Set the event date
        eventDateTextView.text = "Event Date: $formattedDate"

        // Set the event time
        eventTimeTextView.text = "Event Time: ${numberFormat.format(modifiedHour)}:${numberFormat.format(minute)} $amPm"

        linear.addView(cardView)
    }

    private fun getHourAmPm(hour: Int): Int {
        var modifiedHour = if (hour > 11) hour - 12 else hour
        if(modifiedHour == 0){
            modifiedHour = 12
        }
        return modifiedHour
    }
}


