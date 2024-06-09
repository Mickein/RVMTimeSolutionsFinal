package za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.CalendarEvents
import za.co.varsitycollege.st10215473.rvmtimesolutions.R
import za.co.varsitycollege.st10215473.rvmtimesolutions.databinding.EventDetailsBinding
import java.util.Calendar

class CalendarAdapter(private val calendarEventsList: java.util.ArrayList<CalendarEvents>): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    private var clientNameList: ArrayList<String> = arrayListOf()
    private lateinit var autoCompleteText: AutoCompleteTextView
    class ViewHolder(val binding: EventDetailsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(EventDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return calendarEventsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = calendarEventsList[position]
        holder.apply {
            binding.apply {
                exameNameTxt.text = currentItem.eventName
                eventDateTxt.text = currentItem.eventDate
                eventTimeTxt.text = currentItem.eventTime
                if(currentItem.takingLeave == true){
                    txtTakingLeave.setText("Taking Leave")
                }else{
                    txtTakingLeave.setText("")
                }
                if(currentItem.clientName != ""){
                    txtClientName.text = currentItem.clientName
                }
                else{
                    txtClientName.text = ""
                }

                editCalendarEvent.setOnClickListener{
                    editEvent(currentItem, position)
                }

                deleteCalendarEvent.setOnClickListener {
                    showDeleteConfirmation(currentItem.id, position)
                }
            }

        }
    }
    private fun ViewHolder.editEvent(calendarEvents: CalendarEvents, position: Int){
        fetchClientNames()
        val context = binding.root.context
        val dialogBuilder = AlertDialog.Builder(context)
        val editEventForm = LayoutInflater.from(context).inflate(R.layout.add_event, null)

        val eventNameEditText = editEventForm.findViewById<EditText>(R.id.edtEventName)
        val timeButton = editEventForm.findViewById<Button>(R.id.btnAddTime)
        val clientDropdown = editEventForm.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val takingLeaveSwitch = editEventForm.findViewById<SwitchMaterial>(R.id.switchTakingLeave)
        val addButton = editEventForm.findViewById<Button>(R.id.btnAddEvent)

        addButton.setText("Update Event")
        eventNameEditText.setText(calendarEvents.eventName)
        clientDropdown.setText(calendarEvents.clientName)
        timeButton.setText(calendarEvents.eventTime)
        takingLeaveSwitch.isChecked = calendarEvents.takingLeave == true

        val adapter = ArrayAdapter(context, R.layout.dropdown_item, clientNameList)
        clientDropdown.setAdapter(adapter)

        clientDropdown.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            clientDropdown.setText(selectedItem.toString())
        }

        timeButton.setOnClickListener {
            openTimePicker(it, timeButton, context)
        }

        dialogBuilder.setView(editEventForm)
        val dialog = dialogBuilder.create()

        addButton.setOnClickListener {
            val updatedEventName = eventNameEditText.text.toString()
            val updatedTime = timeButton.text.toString()
            val updatedTakingLeave = takingLeaveSwitch.isChecked
            val updatedClientName = clientDropdown.text.toString()

            val updatedEvent = calendarEvents.copy(
                eventName = updatedEventName,
                eventTime = updatedTime,
                clientName = updatedClientName,
                takingLeave = updatedTakingLeave
            )

            updateEventInFirebase(updatedEvent)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun ViewHolder.updateEventInFirebase(calendarEvent: CalendarEvents) {
        val firebaseRef = FirebaseDatabase.getInstance().getReference("CalendarEvents").child(calendarEvent.id ?: "")
        firebaseRef.setValue(calendarEvent).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val position = calendarEventsList.indexOfFirst { it.id == calendarEvent.id }
                if (position != -1) {
                    calendarEventsList[position] = calendarEvent
                    notifyItemChanged(position)
                }
                Toast.makeText(binding.root.context, "Event Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(binding.root.context, "Failed to Update Event", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun openTimePicker(view: View, addTimeButton:Button, context: Context) {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val timeText = makeTimeString(hourOfDay, minute)
            addTimeButton.text = timeText
        }
        val cal = Calendar.getInstance()
        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
            Calendar.MINUTE), true).show()
    }

    private fun makeTimeString(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }
    private fun fetchClientNames(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        FirebaseDatabase.getInstance().getReference("Timesheets").orderByChild("userId").equalTo(uid).addValueEventListener(object:
            ValueEventListener {
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
    private fun ViewHolder.showDeleteConfirmation(eventId: String?, position: Int) {
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setTitle("Delete Event")
        builder.setMessage("Are you sure you want to delete this event?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            deleteEvent(eventId, position)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun ViewHolder.deleteEvent(eventId: String?, position: Int) {
        if (eventId.isNullOrEmpty()) {
            Toast.makeText(binding.root.context, "Event ID is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val firebaseRef = FirebaseDatabase.getInstance().getReference("CalendarEvents").child(eventId)
        firebaseRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                calendarEventsList.removeAt(position)
                notifyItemRemoved(position)
                Toast.makeText(binding.root.context, "Event Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(binding.root.context, "Failed to Delete Event", Toast.LENGTH_SHORT).show()
            }
        }
    }
}