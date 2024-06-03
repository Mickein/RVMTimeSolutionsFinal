package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter.CalendarAdapter
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.CalendarEvents
import za.co.varsitycollege.st10215473.rvmtimesolutions.Decorator.SpacesItemDecoration
import za.co.varsitycollege.st10215473.rvmtimesolutions.databinding.FragmentCalendarBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalendarFragment : Fragment() {
    private lateinit var calendar: CalendarView
    private lateinit var selectedDate: Date
    private lateinit var linear: RecyclerView
    private lateinit var addTimeButton: ImageButton
    private lateinit var eventName: EditText
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var calendarEventsList: ArrayList<CalendarEvents>
    private var binding: FragmentCalendarBinding? = null
    private lateinit var spin: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val addEventView = inflater.inflate(R.layout.add_event, container, false)
        spin = addEventView.findViewById(R.id.spinnerClientName)
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        val items = arrayOf("Coming Soon")
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, items)
        spin.setAdapter(adapter)

        val binding = FragmentCalendarBinding.inflate(inflater, container, false)

        calendar = view.findViewById(R.id.calendarView)

        linear = view.findViewById(R.id.linearLayout)
        firebaseRef = FirebaseDatabase.getInstance().getReference("CalendarEvents")
        calendarEventsList = arrayListOf()

        calendar.setOnDateChangeListener { _, year, month, dayOfMonth->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
            showAddEventDialog()
        }

        fetchData()

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)
        linear.addItemDecoration(SpacesItemDecoration(spacingInPixels))

        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        linear.layoutManager = linearLayoutManager

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
                addEventToFireBase(materialTimePicker.hour, materialTimePicker.minute, eventN)
            }
            materialTimePicker.show(parentFragmentManager, "material_time_picker")
        }
    }

    private fun addEventToFireBase(hour: Int, minute: Int, name: String){
        val modifiedHour = getHourAmPm(hour)
        val amPm = if (hour > 11) "PM" else "AM"
        val numberFormat = DecimalFormat("00")

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = sdf.format(selectedDate)

        val time = "${numberFormat.format(modifiedHour)}:${numberFormat.format(minute)} $amPm"

        if(name.isEmpty()){
            eventName.error = "Add a project name"
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        val eventId = firebaseRef.push().key!!
        val events = CalendarEvents(eventId, formattedDate, name, time, "", uid)

        firebaseRef.child(eventId).setValue(events)
            .addOnCompleteListener {
                Toast.makeText(context, "Event Added Successfully", Toast.LENGTH_SHORT).show()
            }
        view
    }

    private fun fetchData(){
        if (!isAdded) {
            // Fragment is not attached, handle appropriately
            return
        }
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        firebaseRef.orderByChild("userId").equalTo(uid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                calendarEventsList.clear()
                if(snapshot.exists()){
                    for (calendarSnap in snapshot.children){
                        val calendar = calendarSnap.getValue(CalendarEvents::class.java)
                        calendarEventsList.add(calendar!!)
                    }
                }
                val calendarAdapter = CalendarAdapter(calendarEventsList)
                linear.adapter = calendarAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "error: ${error}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getHourAmPm(hour: Int): Int {
        var modifiedHour = if (hour > 11) hour - 12 else hour
        if(modifiedHour == 0){
            modifiedHour = 12
        }
        return modifiedHour
    }
}


