package za.co.varsitycollege.st10215473.rvmtimesolutions.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Timesheets
import za.co.varsitycollege.st10215473.rvmtimesolutions.databinding.ProgressBarBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProgressBarAdapter(private var timesheetList: java.util.ArrayList<Timesheets>) : RecyclerView.Adapter<ProgressBarAdapter.ViewHolder>(){

    init{
        filterDataForLastMonth()
    }
    class ViewHolder(val binding: ProgressBarBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ProgressBarBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return timesheetList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = timesheetList[position]
        holder.apply {
            binding.apply {
                val currentItem = timesheetList[position]
                holder.apply {
                    binding.apply {
                        val maxGoalHours = currentItem.maxHourGoal
                        val minGoalHours = currentItem.minHourGoal
                        val hoursSpent =
                            calculateHoursSpent(currentItem.startTime, currentItem.endTime)
                        val progressPercentage = (hoursSpent / maxGoalHours!!) * 100
                        txtProjectName.text = currentItem.name
                        txtStartDate.text = currentItem.date
                        txtMaxHours.text = "${maxGoalHours}hrs"
                        txtHoursSpent.text = "${hoursSpent}hrs"

                        //Some code by Indently on Youtube: https://www.youtube.com/watch?v=xU-Cc41DfTg
                        progressBar.max = maxGoalHours
                        progressBar.progress = hoursSpent.toInt()

                        percentageCompleted.text = "${progressPercentage.toInt()}% Completed"
                    }
                }
            }
        }
    }
    private fun calculateHoursSpent(startTime: String?, endTime: String?): Double {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        var hours: Double = 0.0
        try {
            // Parse start time and end time strings
            val start = format.parse(startTime)
            val end = format.parse(endTime)

            if (end.before(start)) {//if end time ends the next day
                val calendar = Calendar.getInstance()
                calendar.time = end
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                end.time = calendar.timeInMillis
            }

            // Calculate time difference in milliseconds
            val timeDifference = end.time - start.time

            hours =  timeDifference / (1000 * 60 * 60).toDouble()
        } catch (e: Exception) {

            e.printStackTrace()
        }

        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(hours).toDouble()
    }
    private fun filterDataForLastMonth() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val oneMonthAgo = calendar.time

        val dateFormat = SimpleDateFormat("MMM d yyyy", Locale.ENGLISH)
        timesheetList = timesheetList.filter { timesheet ->
            val date = dateFormat.parse(timesheet.date)
            date.after(oneMonthAgo)
        } as ArrayList<Timesheets>
    }
}