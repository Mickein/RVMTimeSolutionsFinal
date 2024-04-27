package za.co.varsitycollege.st10215473.rvmtimesolutions.CalendarAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Timesheets
import za.co.varsitycollege.st10215473.rvmtimesolutions.R
import za.co.varsitycollege.st10215473.rvmtimesolutions.databinding.TimesheetCardviewBinding

class TimesheetAdapter(private val timesheetList: java.util.ArrayList<Timesheets>): RecyclerView.Adapter<TimesheetAdapter.ViewHolder>() {

    class ViewHolder(val binding: TimesheetCardviewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TimesheetCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return timesheetList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = timesheetList[position]
        holder.apply {
            binding.apply {
                if(currentItem.image != null){
                    txtHeading.text = currentItem.name
                    txtNameCardView.text = currentItem.clientName
                    Picasso.get().load(currentItem.image).into(imgCardView)
                }
                else{
                    txtHeading.text = currentItem.name
                    txtNameCardView.text = currentItem.clientName
                    imgCardView.setImageResource(R.drawable.default_photo_timesheet)
                }
            }
        }
    }
}