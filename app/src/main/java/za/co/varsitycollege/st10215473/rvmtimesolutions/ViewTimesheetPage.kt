package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter.TimesheetAdapter
import za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter.ViewTimesheetAdapter
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Timesheets
import za.co.varsitycollege.st10215473.rvmtimesolutions.Decorator.SpacesItemDecoration

class ViewTimesheetPage : AppCompatActivity() {
    private lateinit var rvViewTimesheet: RecyclerView
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var timesheetList: ArrayList<Timesheets>
    private lateinit var query: Query
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_timesheet)

        timesheetList = arrayListOf()
        firebaseRef = FirebaseDatabase.getInstance().reference.child("Timesheets")
        rvViewTimesheet = findViewById(R.id.rvViewTimesheet)

        fetchData()

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvViewTimesheet.layoutManager = linearLayoutManager
    }

    private fun fetchData() {
        val timesheetId = intent.getStringExtra("id")

        firebaseRef.orderByChild("id").equalTo(timesheetId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                timesheetList.clear()
                if(snapshot.exists()){
                    for (timesheetSnap in snapshot.children){
                        val timesheetCard = timesheetSnap.getValue(Timesheets::class.java)
                        timesheetCard?.let {
                            timesheetList.add(it)
                        }
                    }
                    val viewTimesheetAdapter = ViewTimesheetAdapter(timesheetList)
                    val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_items)
                    rvViewTimesheet.addItemDecoration(SpacesItemDecoration(spacingInPixels))
                    rvViewTimesheet.adapter = viewTimesheetAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}