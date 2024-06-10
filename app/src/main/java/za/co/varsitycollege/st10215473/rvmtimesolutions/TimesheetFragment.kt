package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.whenCreated
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter.CalendarAdapter
import za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter.TimesheetAdapter
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.CalendarEvents
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Timesheets
import za.co.varsitycollege.st10215473.rvmtimesolutions.Decorator.SpacesItemDecoration
import java.util.Calendar


class TimesheetFragment : Fragment(), TimesheetAdapter.OnTimesheetClickListener{
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var rvTimesheetCardView: RecyclerView
    private lateinit var timesheetList: ArrayList<Timesheets>
    private lateinit var autoCompleteText: AutoCompleteTextView
    private lateinit var query: Query
    private lateinit var auth: FirebaseAuth
    private var oldestToNew: Boolean = false
    private lateinit var startDatePickerDialog: DatePickerDialog
    private lateinit var endDatePickerDialog: DatePickerDialog
    private var date: String? = null
    private lateinit var startDateButton: Button
    private lateinit var endDateButton: Button
    private lateinit var filterOkButton: Button
    private lateinit var filterCancelButton: Button

    override fun onResume() {
        super.onResume()
        //Some code by Stevdza-San on Youtube: https://www.youtube.com/watch?v=741l_fPKL3Y
        val items = resources.getStringArray(R.array.sortBy)
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, items)
        autoCompleteText.setAdapter(adapter)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timesheet, container, false)

        timesheetList = arrayListOf()
        autoCompleteText = view.findViewById(R.id.autoCompleteTextView)
        rvTimesheetCardView = view.findViewById(R.id.rvTimesheet)
        firebaseRef = FirebaseDatabase.getInstance().reference.child("Timesheets")
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        query = firebaseRef.orderByChild("userId").equalTo(userId)
        fetchData(query)

        autoCompleteText.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            if(selectedItem == "All Entries"){
                query = firebaseRef.orderByChild("userId").equalTo(userId)
                fetchData(query)
            }
            else if(selectedItem == "Project Name"){
                getProjectNames {names ->
                    val options = names.toTypedArray()
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Choose a Project Name")
                    builder.setItems(options) { dialogInterface: DialogInterface, which: Int ->
                        val selectedName = options[which]
                        query = firebaseRef.orderByChild("name").equalTo(selectedName)
                        fetchData(query)
                        dialogInterface.dismiss()
                    }
                    builder.show()
                }
            }
            else if(selectedItem == "Category"){
                getCategories {categories ->
                    val options = categories.toTypedArray()
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Choose a Category")
                    builder.setItems(options) { dialogInterface: DialogInterface, which: Int ->
                        val selectedCategory = options[which]
                        query = firebaseRef.orderByChild("category").equalTo(selectedCategory)
                        fetchData(query)
                        dialogInterface.dismiss()
                    }
                    builder.show()
                }
            }
            else if(selectedItem == "Newest to Oldest"){
                query = firebaseRef.orderByChild("timestamp")
                fetchData(query)
            }
            else if(selectedItem == "Oldest to Newest"){
                oldestToNew = true
                query = firebaseRef.orderByChild("timestamp")
                fetchData(query)
            }
            else if(selectedItem == "Date"){
                val dialogBuilder = AlertDialog.Builder(requireContext())
                val dateForm = layoutInflater.inflate(R.layout.filter_date, null)
                dialogBuilder.setView(dateForm)
                val dialog = dialogBuilder.create()
                dialog.show()

                startDateButton = dateForm.findViewById(R.id.btnStartDate)
                endDateButton = dateForm.findViewById(R.id.btnEndDate)
                filterOkButton = dateForm.findViewById(R.id.btnFilterOk)
                filterCancelButton = dateForm.findViewById(R.id.btnFilterCancel)

                startDateButton.setOnClickListener {
                    openStartDatePicker(it)
                }

                endDateButton.setOnClickListener {
                    openEndDatePicker(it)
                }

                filterOkButton.setOnClickListener {
                    val startDate = startDateButton.text.toString()
                    val endDate = endDateButton.text.toString()

                    query = firebaseRef
                        .orderByChild("date")
                        .startAt(startDate)
                        .endAt(endDate)
                    fetchData(query)
                    dialog.hide()
                }
                filterCancelButton.setOnClickListener {
                    dialog.hide()
                }

            }
        }

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)
        rvTimesheetCardView.addItemDecoration(SpacesItemDecoration(spacingInPixels))
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvTimesheetCardView.layoutManager = linearLayoutManager

        return view
    }

    private fun getProjectNames(callback: (List<String>)-> Unit){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        val names = ArrayList<String>()
        firebaseRef.orderByChild("userId").equalTo(uid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(dataSnapshot in snapshot.children){
                        val name = dataSnapshot.child("name").value.toString()
                        if (!names.contains(name)){
                            names.add(name)
                        }
                    }
                }
                callback(names)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getCategories(callback: (List<String>) -> Unit){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        val categories = ArrayList<String>()
        firebaseRef.orderByChild("userId").equalTo(uid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(dataSnapshot in snapshot.children){
                        val category = dataSnapshot.child("category").value.toString()
                        if (!categories.contains(category)){
                            categories.add(category)
                        }
                    }
                }
                callback(categories)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun fetchData(query: Query){
        if (!isAdded) {

            return
        }

        timesheetList.clear()
        rvTimesheetCardView.adapter = null

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (timesheetSnap in snapshot.children){
                        val timesheetCard = timesheetSnap.getValue(Timesheets::class.java)
                        val userId = timesheetSnap.child("userId").value.toString()
                        if(userId == uid){
                            timesheetCard?.let {
                                timesheetList.add(it)
                            }
                        }
                    }
                    if(oldestToNew == true){
                        timesheetList.reverse()
                        oldestToNew = false
                    }
                    if(timesheetList.isEmpty()){
                        Toast.makeText(requireContext(), "There are no entries in that range.", Toast.LENGTH_SHORT).show()
                        rvTimesheetCardView.adapter = null
                    }else{
                        val timesheetAdapter = TimesheetAdapter(timesheetList, this@TimesheetFragment)
                        rvTimesheetCardView.adapter = timesheetAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    // Access context here only if the fragment is attached
                    Toast.makeText(requireActivity().applicationContext, "error: $error", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
    override fun onTimesheetClicked(timesheetId: String?) {
        // Handle item click here, for example, navigate to the details activity
        val intent = Intent(requireContext(), ViewTimesheetPage::class.java).apply {
            putExtra("id", timesheetId)
        }
        startActivity(intent)
    }

    private fun initStartDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val adjustedMonth = month + 1
            startDateButton.text = makeDateString(dayOfMonth, adjustedMonth, year)
        }
        val cal = Calendar.getInstance()
        val style = AlertDialog.THEME_HOLO_LIGHT
        startDatePickerDialog = DatePickerDialog(requireContext(), style, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }

    private fun initEndDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val adjustedMonth = month + 1
            endDateButton.text = makeDateString(dayOfMonth, adjustedMonth, year)
        }
        val cal = Calendar.getInstance()
        val style = AlertDialog.THEME_HOLO_LIGHT
        endDatePickerDialog = DatePickerDialog(requireContext(), style, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return getMonthFormat(month) + " " + day + " " + year
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

    fun openStartDatePicker(view: View) {
        if (!::startDatePickerDialog.isInitialized) {
            initStartDatePicker()
        }
        startDatePickerDialog.show()
    }

    fun openEndDatePicker(view: View) {
        if (!::endDatePickerDialog.isInitialized) {
            initEndDatePicker()
        }
        endDatePickerDialog.show()
    }
}