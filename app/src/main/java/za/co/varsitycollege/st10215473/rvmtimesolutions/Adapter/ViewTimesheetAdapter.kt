package za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Timesheets
import za.co.varsitycollege.st10215473.rvmtimesolutions.R
import za.co.varsitycollege.st10215473.rvmtimesolutions.databinding.EventDetailsBinding
import za.co.varsitycollege.st10215473.rvmtimesolutions.databinding.ViewTimesheetCardviewBinding

class ViewTimesheetAdapter(private val timesheetList:java.util.ArrayList<Timesheets>): RecyclerView.Adapter<ViewTimesheetAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewTimesheetCardviewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ViewTimesheetCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return timesheetList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = timesheetList[position]
        holder.apply {
            binding.apply {
                if (!currentItem.image.isNullOrEmpty()) {
                    Picasso.get().load(currentItem.image).into(imgViewPhoto)
                } else {
                    imgViewPhoto.setImageResource(R.drawable.default_photo_timesheet)
                }
                txtViewProjectName.text = currentItem.name
                txtViewClient.text = currentItem.clientName
                txtViewCategory.text = currentItem.category
                txtViewDescription.text = currentItem.description
                txtViewDate.text = currentItem.date
                txtViewStartTime.text = currentItem.startTime
                txtViewEndTime.text = currentItem.endTime
                txtViewMinGoal.text = currentItem.minHourGoal.toString()
                txtViewMaxGoal.text = currentItem.maxHourGoal.toString()
            }
        }
    }

}