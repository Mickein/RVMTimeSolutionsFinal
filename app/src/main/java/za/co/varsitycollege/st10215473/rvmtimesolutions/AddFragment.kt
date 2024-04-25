package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.app.DatePickerDialog
import android.os.Bundle
import android.provider.ContactsContract.RawContacts.Data
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.CalendarEvents
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Timesheets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddFragment : Fragment() {
    private lateinit var dateText: EditText
    private lateinit var categoryText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var minGoalText: EditText
    private lateinit var maxGoalText: EditText
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var timesheetsList: ArrayList<Timesheets>
    private lateinit var captureTimesheetButton: Button
    private lateinit var projectNameText: EditText
    private lateinit var clientNameText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        captureTimesheetButton = view.findViewById(R.id.btncaptureentryADD)
        projectNameText = view.findViewById(R.id.edtprojectnameAdd)
        clientNameText = view.findViewById(R.id.txtclientNameAdd)
        categoryText = view.findViewById(R.id.edtcreateCategoryAdd)
        descriptionText = view.findViewById(R.id.edtDescriptionAdd)
        minGoalText = view.findViewById(R.id.edtmingoalhourAdd)
        maxGoalText = view.findViewById(R.id.edtmaxgoalhourADD)
        dateText = view.findViewById(R.id.edtdateAdd)

        firebaseRef = FirebaseDatabase.getInstance().getReference("Timesheets")
        timesheetsList = arrayListOf()
        storageRef = FirebaseStorage.getInstance().getReference("images")

        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener{ view, year, month, dayofMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayofMonth)
            dateToText(calendar)
        }

        dateText.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show()
        }

        captureTimesheetButton.setOnClickListener(){

        }

        return view.rootView
    }

    fun dateToText(calendar: Calendar){
        val format = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(format, Locale.UK)
        dateText.setText(sdf.format(calendar.time))
    }
    fun captureTimesheet(){
        val name = projectNameText.text.toString()
        val category = categoryText.text.toString()
        val description = descriptionText.text.toString()
        val minGoal = minGoalText.text.toString()
        val maxGoal = maxGoalText.text.toString()
        val client = clientNameText.text.toString()
        val date = dateText.text.toString()

        val eventId = firebaseRef.push().key!!
        val events = Timesheets(eventId, name, date, )

        firebaseRef.child(eventId).setValue(events)
            .addOnCompleteListener {
                Toast.makeText(context, "Event Added Successfully", Toast.LENGTH_SHORT).show()
            }
        view
    }
}