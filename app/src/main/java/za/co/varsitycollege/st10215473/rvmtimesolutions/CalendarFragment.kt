package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.green
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
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
    private lateinit var addTimeButton: Button
    private lateinit var eventName: EditText
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var calendarEventsList: ArrayList<CalendarEvents>
    private lateinit var autoCompleteText: AutoCompleteTextView
    private lateinit var clientNameList: ArrayList<String>
    private lateinit var takingLeaveSwitch: SwitchMaterial
    private lateinit var deleteEvent: ImageView
    private lateinit var editEvent: ImageView
    private lateinit var addEventButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        val eventForm = layoutInflater.inflate(R.layout.event_details, null)
        deleteEvent = eventForm.findViewById(R.id.deleteCalendarEvent)
        editEvent = eventForm.findViewById(R.id.editCalendarEvent)
        clientNameList = arrayListOf()

        deleteEvent.setOnClickListener{
            Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
        }

        fetchClientNames()

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

    private fun fetchClientNames(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        FirebaseDatabase.getInstance().getReference("Timesheets").orderByChild("userId").equalTo(uid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(dataSnapshot in snapshot.children){
                        val name = dataSnapshot.child("clientName").value.toString()
                        if (!clientNameList.contains(name)){
                            clientNameList.add(name)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun showAddEventDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val eventForm = layoutInflater.inflate(R.layout.add_event, null)
        dialogBuilder.setView(eventForm)
        val dialog = dialogBuilder.create()
        dialog.show()

        addEventButton = eventForm.findViewById(R.id.btnAddEvent)
        takingLeaveSwitch = eventForm.findViewById(R.id.switchTakingLeave)
        autoCompleteText = eventForm.findViewById(R.id.autoCompleteTextView)
        addTimeButton = eventForm.findViewById(R.id.btnAddTime)
        eventName = eventForm.findViewById(R.id.edtEventName)
        var clientName: String = ""

        val adapter = ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, clientNameList)
        autoCompleteText.setAdapter(adapter)

        autoCompleteText.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            clientName = selectedItem.toString()
        }

        addTimeButton.setOnClickListener {
            openTimePicker(it)
        }

        addEventButton.setOnClickListener {
            dialog.hide()
            val eventN = eventName.text.toString()
            val leave = takingLeaveSwitch.isChecked
            val time = addTimeButton.text.toString()
            addEventToFireBase(time, eventN, leave, clientName)
        }
    }

    private fun openTimePicker(view: View) {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val timeText = makeTimeString(hourOfDay, minute)
            addTimeButton.text = timeText
        }
        val cal = Calendar.getInstance()
        TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    private fun makeTimeString(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }

    private fun addEventToFireBase(time: String, name: String, leave:Boolean, clientName: String){
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = sdf.format(selectedDate)
        if(name.isEmpty()){
            eventName.error = "Add an event name"
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        val eventId = firebaseRef.push().key!!
        val events = CalendarEvents(eventId, formattedDate, name, time, "", leave, clientName, uid)

        firebaseRef.child(eventId).setValue(events)
            .addOnCompleteListener { task ->
                Toast.makeText(context, "Event Added Successfully", Toast.LENGTH_SHORT).show()
            }
        view
    }

    private fun fetchData(){
        val eventForm = layoutInflater.inflate(R.layout.add_event, null)
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


