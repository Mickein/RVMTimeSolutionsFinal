package za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Timesheets
import za.co.varsitycollege.st10215473.rvmtimesolutions.R
import za.co.varsitycollege.st10215473.rvmtimesolutions.databinding.TimesheetCardviewBinding

class TimesheetAdapter(private val timesheetList: java.util.ArrayList<Timesheets>, private val clickListener: OnTimesheetClickListener): RecyclerView.Adapter<TimesheetAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: TimesheetCardviewBinding): RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val timesheetId = timesheetList[adapterPosition].id
            clickListener.onTimesheetClicked(timesheetId)
        }
    }

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
                if(!currentItem.image.isNullOrEmpty()){
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

    interface OnTimesheetClickListener {
        fun onTimesheetClicked(timesheetId: String?)
    }
}