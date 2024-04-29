package za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.CalendarEvents
import za.co.varsitycollege.st10215473.rvmtimesolutions.databinding.EventDetailsBinding

class CalendarAdapter(private val calendarEventsList: java.util.ArrayList<CalendarEvents>): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

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
            }

        }
    }
}