package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter.CalendarAdapter
import za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter.TimesheetAdapter
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.CalendarEvents
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Timesheets
import za.co.varsitycollege.st10215473.rvmtimesolutions.Decorator.SpacesItemDecoration


class TimesheetFragment : Fragment() {
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var rvTimesheetCardView: RecyclerView
    private lateinit var timesheetList: ArrayList<Timesheets>
    private lateinit var filterSpinner: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_timesheet, container, false)

        timesheetList = arrayListOf()
        filterSpinner = view.findViewById(R.id.sprTimeSheetFilter)
        rvTimesheetCardView = view.findViewById(R.id.rvTimesheet)
        firebaseRef = FirebaseDatabase.getInstance().getReference("Timesheets")

        val items = listOf("All Entries", "Category", "Most Recent", "Oldest", "Date")
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        filterSpinner.adapter = adapter


        fetchData()

        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvTimesheetCardView.layoutManager = linearLayoutManager

        return view
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
                timesheetList.clear()
                if(snapshot.exists()){
                    for (timesheetSnap in snapshot.children){
                        val timesheetCard = timesheetSnap.getValue(Timesheets::class.java)
                        timesheetList.add(timesheetCard!!)
                    }

                    val timesheetAdapter = TimesheetAdapter(timesheetList)
                    val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)
                    rvTimesheetCardView.addItemDecoration(SpacesItemDecoration(spacingInPixels))
                    rvTimesheetCardView.adapter = timesheetAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    // Access context here only if the fragment is attached
                    Toast.makeText(requireContext(), "error: $error", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}