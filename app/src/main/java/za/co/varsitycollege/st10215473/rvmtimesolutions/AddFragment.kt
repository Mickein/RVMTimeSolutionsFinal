package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    private lateinit var addImage: ImageView
    private var uri: Uri? = null

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateButton: Button
    private lateinit var startTimeButton: Button
    private lateinit var endTimeButton: Button
    private var addedAnImage: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        dateButton = view.findViewById(R.id.btnDatePicker)
        startTimeButton = view.findViewById(R.id.btnStartTimePicker)
        endTimeButton = view.findViewById(R.id.btnEndTimePicker)
        captureTimesheetButton = view.findViewById(R.id.btncaptureentryADD)
        projectNameText = view.findViewById(R.id.edtprojectnameAdd)
        clientNameText = view.findViewById(R.id.txtclientNameAdd)
        categoryText = view.findViewById(R.id.edtcreateCategoryAdd)
        descriptionText = view.findViewById(R.id.edtDescription)
        minGoalText = view.findViewById(R.id.edtmingoalhourAdd)
        maxGoalText = view.findViewById(R.id.edtmaxgoalhourADD)
        addImage = view.findViewById(R.id.imageView8)
        firebaseRef = FirebaseDatabase.getInstance().getReference("Timesheets")
        timesheetsList = arrayListOf()
        storageRef = FirebaseStorage.getInstance().getReference("images")

        dateButton.text = getTodaysDate()
        startTimeButton.text = getCurrentTime()
        endTimeButton.text = getCurrentTime()

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
            addImage.setImageURI(it)
            if(it != null){
                uri = it
            }
        }

        addImage.setOnClickListener{
            addedAnImage = true
            pickImage.launch("image/*")
        }

        dateButton.setOnClickListener {
            openDatePicker(it)
        }
        startTimeButton.setOnClickListener {
            openTimePicker(it, true)
        }
        endTimeButton.setOnClickListener {
            openTimePicker(it, false)
        }
        captureTimesheetButton.setOnClickListener{
            captureTimesheet()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDatePicker() // Initialize the date picker after the view is created
    }
    fun captureTimesheet(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val name = projectNameText.text.toString()
        val category = categoryText.text.toString()
        val description = descriptionText.text.toString()
        val minGoal = minGoalText.text.toString()
        val minHour = minGoal.toInt()
        val maxGoal = maxGoalText.text.toString()
        val maxHour = maxGoal.toInt()
        val client = clientNameText.text.toString()
        val date = dateButton.text.toString()
        val startTime = startTimeButton.text.toString()
        val endTime = endTimeButton.text.toString()

        if(name.isEmpty()){
            projectNameText.error = "Add a Project Name"
            return
        }
        if(category.isEmpty()) {
            categoryText.error = "Add a Category"
            return
        }
        if(description.isEmpty()) {
            descriptionText.error = "Add a Description"
            return
        }
        if(minGoal.isEmpty()) {
            minGoalText.error = "Add a minimum hour goal"
            return
        }
        if(maxGoal.isEmpty()) {
            maxGoalText.error = "Add a maximum hour goal"
            return
        }
        if(client.isEmpty()) {
            clientNameText.error = "Add a client name"
            return
        }

        val timesheetId = firebaseRef.push().key!!
        var timesheets: Timesheets

        val uid = currentUser?.uid

        if(addedAnImage == true){
            uri?.let {
                storageRef.child(timesheetId).putFile(it)
                    .addOnSuccessListener {task->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener {url ->
                                val imgUrl = url.toString()
                                timesheets = Timesheets(category, client, date, description, endTime, timesheetId, imgUrl, maxHour, minHour, name, startTime, uid)
                                firebaseRef.child(timesheetId).setValue(timesheets)
                                    .addOnCompleteListener {
                                        Toast.makeText(context, "Timesheet Captured Successfully", Toast.LENGTH_SHORT).show()

                                        projectNameText.setText("")
                                        categoryText.setText("")
                                        descriptionText.setText("")
                                        minGoalText.setText("")
                                        maxGoalText.setText("")
                                        clientNameText.setText("")
                                        dateButton.text = getTodaysDate()
                                        startTimeButton.text = getCurrentTime()
                                        endTimeButton.text = getCurrentTime()
                                    }
                                view
                            }
                    }
            }
        }
        else{
            timesheets = Timesheets(category, client, date, description, endTime, timesheetId, "", maxHour, minHour, name, startTime, uid)
            firebaseRef.child(timesheetId).setValue(timesheets)
                .addOnCompleteListener {
                    Toast.makeText(context, "Timesheet Captured Failed", Toast.LENGTH_SHORT).show()
                }
            view
        }
    }

    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    private fun getCurrentTime(): String {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        return makeTimeString(hour, minute)
    }

    private fun initDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val adjustedMonth = month + 1
            dateButton.text = makeDateString(dayOfMonth, adjustedMonth, year)
        }
        val cal = Calendar.getInstance()
        val style = AlertDialog.THEME_HOLO_LIGHT
        datePickerDialog = DatePickerDialog(requireContext(), style, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }

    private fun openTimePicker(view: View, isStartTime: Boolean) {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val timeText = makeTimeString(hourOfDay, minute)
            if (isStartTime) {
                startTimeButton.text = timeText
            } else {
                endTimeButton.text = timeText
            }
        }
        val cal = Calendar.getInstance()
        TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun makeTimeString(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }

    private fun getMonthFormat(month: Int): String {
        return when (month) {
            1 -> "JAN"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "APR"
            5 -> "MAY"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AUG"
            9 -> "SEP"
            10 -> "OCT"
            11 -> "NOV"
            12 -> "DEC"
            else -> "JAN" // Default should never happen
        }
    }

    fun openDatePicker(view: View) {
        if (!::datePickerDialog.isInitialized) {
            initDatePicker()
        }
        datePickerDialog.show()
    }

}