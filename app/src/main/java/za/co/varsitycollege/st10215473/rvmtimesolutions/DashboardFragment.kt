package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import za.co.varsitycollege.st10215473.rvmtimesolutions.Data.Timesheets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DashboardFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var progressBar: ProgressBar
    private lateinit var seekBar: SeekBar
    private lateinit var barChart: BarChart
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var query: Query
    private lateinit var queryBarChart: Query
    private lateinit var startDatePickerDialog: DatePickerDialog
    private lateinit var endDatePickerDialog: DatePickerDialog
    private lateinit var barStartPickerDialog: DatePickerDialog
    private lateinit var barEndPickerDialog: DatePickerDialog
    private lateinit var startDateButton: Button
    private lateinit var endDateButton: Button
    private lateinit var barStartDate: Button
    private lateinit var barEndDate: Button
    private lateinit var viewAllButton: Button
    private lateinit var viewPeriodButton: Button
    private lateinit var barViewPeriod: Button
    private lateinit var barViewCurrentWeek: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        firebaseRef = FirebaseDatabase.getInstance().reference.child("Timesheets")

        viewPeriodButton = view.findViewById(R.id.btnViewPeriod)
        viewAllButton = view.findViewById(R.id.btnViewAll)
        endDateButton = view.findViewById(R.id.btnEndDatePicker)
        startDateButton = view.findViewById(R.id.btnStartDatePicker)
        pieChart = view.findViewById(R.id.pie_chart)
        barChart = view.findViewById(R.id.bar_chart)
        barStartDate = view.findViewById(R.id.btnBarGraphStart)
        barEndDate = view.findViewById(R.id.btnBarGraphEnd)
        barViewPeriod = view.findViewById(R.id.btnBarView)
        barViewCurrentWeek = view.findViewById(R.id.btnViewCurrentWeek)

        barStartDate.text = getTodaysDate()
        barEndDate.text = getTodaysDate()
        startDateButton.text = getTodaysDate()
        endDateButton.text = getTodaysDate()

        query = firebaseRef.orderByChild("userId").equalTo(userId)
        setPieChartData(query)

        val (startOfWeek, endOfWeek) = getCurrentWeek()

        queryBarChart = firebaseRef
            .orderByChild("date")
            .startAt(startOfWeek)
            .endAt(endOfWeek)
        setBarChartData(queryBarChart)

        viewAllButton.setOnClickListener {
            query = firebaseRef.orderByChild("userId").equalTo(userId)
            setPieChartData(query)
        }

        barViewCurrentWeek.setOnClickListener {
            queryBarChart = firebaseRef
                .orderByChild("date")
                .startAt(startOfWeek)
                .endAt(endOfWeek)
            setBarChartData(queryBarChart)
        }

        startDateButton.setOnClickListener{
            openStartDatePicker(it)
        }
        endDateButton.setOnClickListener{
            openEndDatePicker(it)
        }

        barStartDate.setOnClickListener{
            openBarStartDatePicker(it)
        }

        barEndDate.setOnClickListener {
            openBarEndDatePicker(it)
        }

        viewPeriodButton.setOnClickListener {
            val startDate = startDateButton.text.toString()
            val endDate = endDateButton.text.toString()

            query = firebaseRef
                .orderByChild("date")
                .startAt(startDate)
                .endAt(endDate)
            setPieChartData(query)
        }

        barViewPeriod.setOnClickListener {
            val startDate = barStartDate.text.toString()
            val endDate = barEndDate.text.toString()

            queryBarChart = firebaseRef
                .orderByChild("date")
                .startAt(startDate)
                .endAt(endDate)
            setBarChartData(queryBarChart)
        }

        return view
    }

    private fun setPieChartData(query: Query){
        pieChart.clear()
        pieChart.invalidate()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val categoryHoursMap = HashMap<String, Double>()
                    for (dataSnapshot in snapshot.children){
                        val category = dataSnapshot.child("category").value.toString()
                        val startTime = dataSnapshot.child("startTime").value.toString()
                        val endTime = dataSnapshot.child("endTime").value.toString()
                        val userId = dataSnapshot.child("userId").value.toString()
                        if(userId == uid){
                            val hoursSpent = calculateHoursSpent(startTime, endTime)

                            if(categoryHoursMap.containsKey(category)){
                                categoryHoursMap[category] = categoryHoursMap[category]!! + hoursSpent
                            }
                            else{
                                categoryHoursMap[category] = hoursSpent
                            }
                        }
                    }
                    setUpPieChart(categoryHoursMap)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
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
            // Handle parse errors or other exceptions
            e.printStackTrace()
        }
        return hours
    }

    private fun setUpPieChart(categoryHoursMap: HashMap<String, Double>){

        val pieEntries = ArrayList<PieEntry>()
        for((category, hours) in categoryHoursMap){
            pieEntries.add(PieEntry(hours.toFloat(), category))
        }

        val dataSet = PieDataSet(pieEntries, "Category Hours")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val data = PieData(dataSet)

        dataSet.valueTextSize = 12f

        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = true
        pieChart.legend.textSize = 12f
        pieChart.legend.textColor = Color.BLACK
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setUsePercentValues(false)
        pieChart.animateY(750)

        pieChart.data = data
        pieChart.centerText = "Total Hours Worked Per Category"
        pieChart.setCenterTextSize(15f)
        pieChart.setCenterTextColor(Color.BLACK)
        pieChart.setCenterTextOffset(0f, 0f)
        pieChart.setCenterTextRadiusPercent(100f)
        pieChart.setDrawCenterText(true)
        pieChart.setHoleColor(Color.TRANSPARENT)

        pieChart.invalidate()
    }

    private fun setUpBarChart(totalHoursWorked: Map<String, Float>, minGoals: Map<String, Float>, maxGoals: Map<String, Float>) {
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setPinchZoom(true)
        barChart.setDrawGridBackground(false)
        barChart.setTouchEnabled(false)
        barChart.animateY(1000)
        barChart.setDrawGridBackground(false)

        val days = arrayOf("Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun")

        val values1 = ArrayList<BarEntry>()
        val values2 = ArrayList<BarEntry>()
        val values3 = ArrayList<BarEntry>()

        for (i in days.indices) {
            val day = days[i]
            values1.add(BarEntry(i.toFloat(), totalHoursWorked[day] ?: 0f))
            values2.add(BarEntry(i.toFloat(), maxGoals[day] ?: 0f))
            values3.add(BarEntry(i.toFloat(), minGoals[day] ?: 0f))
        }

        val set1 = BarDataSet(values1, "Hours Worked")
        set1.color = Color.parseColor("#A5BEEF")

        val set2 = BarDataSet(values2, "Max Hours Goal")
        set2.color = Color.parseColor("#3F68BA")

        val set3 = BarDataSet(values3, "Min Hours Goal")
        set3.color = Color.parseColor("#7B9BDA")

        val data = BarData(set1, set2, set3)
        data.barWidth = 0.16f

        barChart.data = data

        // Set labels for X-axis
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(days)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)

        // Center the group of bars
        val groupSpace = 0.4f
        val barSpace = 0.04f
        val barWidth = 0.16f
        data.barWidth = barWidth
        barChart.groupBars(0f, groupSpace, barSpace)

        // Adjust chart padding
        barChart.setExtraOffsets(50f, 0f, 50f, 30f) // Add right padding

        // Set the maximum value for the y-axis
        barChart.axisLeft.axisMaximum = 12f
        barChart.axisRight.axisMaximum = 12f

        // Set the viewport to display all data points
        val visibleRange = days.size + groupSpace * 2 + barSpace * (days.size - 1)
        barChart.setVisibleXRangeMinimum(visibleRange)
        barChart.setVisibleXRangeMaximum(visibleRange)
        barChart.moveViewToX(0f) // Start from the beginning of the chart

        // Set chart title
        barChart.description.isEnabled = true
        barChart.description.text = "Total hours worked each Day with the Min and Max Goals\n"
        barChart.description.textSize = 12f
        barChart.description.textColor = Color.BLACK
        barChart.description.textAlign = Paint.Align.CENTER
        barChart.description.setPosition(600f, 25f)

        barChart.animateY(1500, Easing.EaseInOutQuart)

        barChart.invalidate()

        // Enable legend
        val legend = barChart.legend
        legend.isEnabled = true

        // Customize legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        // Define legend entries
        val greenEntry = LegendEntry().apply {
            formColor = Color.parseColor("#A5BEEF")
            label = "Hours Worked"
        }

        val redEntry = LegendEntry().apply {
            formColor = Color.parseColor("#3F68BA")
            label = "Max Hours Goal"
        }

        val blueEntry = LegendEntry().apply {
            formColor = Color.parseColor("#7B9BDA")
            label = "Min Hours Goal"
        }

        // Set legend entries
        legend.setCustom(arrayOf(greenEntry, redEntry, blueEntry))
    }



    private fun setBarChartData(query: Query) {
        barChart.clear()
        barChart.invalidate()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val totalHoursWorked = mutableMapOf<String, Float>()
                    val minGoals = mutableMapOf<String, Float>()
                    val maxGoals = mutableMapOf<String, Float>()

                    for (day in arrayOf("Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun")) {
                        totalHoursWorked[day] = 0f
                        minGoals[day] = Float.MAX_VALUE
                        maxGoals[day] = 0f
                    }

                    val format = SimpleDateFormat("MMM d yyyy", Locale.getDefault())
                    val calendar = Calendar.getInstance()

                    for (dataSnapshot in snapshot.children) {
                        val dateStr = dataSnapshot.child("date").value.toString()
                        val startTime = dataSnapshot.child("startTime").value.toString()
                        val endTime = dataSnapshot.child("endTime").value.toString()
                        val userId = dataSnapshot.child("userId").value.toString()
                        val minGoal = dataSnapshot.child("minHourGoal").value.toString().toFloat()
                        val maxGoal = dataSnapshot.child("maxHourGoal").value.toString().toFloat()

                        if (userId == uid) {
                            try {
                                val date = format.parse(dateStr)
                                calendar.time = date
                                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                                val day = when (dayOfWeek) {
                                    Calendar.MONDAY -> "Mon"
                                    Calendar.TUESDAY -> "Tues"
                                    Calendar.WEDNESDAY -> "Wed"
                                    Calendar.THURSDAY -> "Thurs"
                                    Calendar.FRIDAY -> "Fri"
                                    Calendar.SATURDAY -> "Sat"
                                    Calendar.SUNDAY -> "Sun"
                                    else -> null
                                }

                                if (day != null) {
                                    val hoursSpent = calculateHoursSpent(startTime, endTime).toFloat()
                                    totalHoursWorked[day] = totalHoursWorked.getOrDefault(day, 0f) + hoursSpent
                                    minGoals[day] = minGoals.getOrDefault(day, Float.MAX_VALUE).coerceAtMost(minGoal)
                                    maxGoals[day] = maxGoals.getOrDefault(day, 0f).coerceAtLeast(maxGoal)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    for (day in minGoals.keys) {
                        if (minGoals[day] == Float.MAX_VALUE) {
                            minGoals[day] = 0f
                        }
                    }
                    // Call a method to set the bar chart data
                    setUpBarChart(totalHoursWorked, minGoals, maxGoals)

                }else {
                    Log.d("DashboardFragment", "No data found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DashboardFragment", "Database error: ${error.message}")
            }
        })
    }

    private fun getCurrentWeek(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY

        // Set the calendar to the start of the week
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val startOfWeek = calendar.time

        // Set the calendar to the end of the week
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.time

        val dateFormat = SimpleDateFormat("MMM d yyyy", Locale.getDefault())
        val startFormatted = dateFormat.format(startOfWeek).uppercase(Locale.getDefault())
        val endFormatted = dateFormat.format(endOfWeek).uppercase(Locale.getDefault())
        return Pair(startFormatted, endFormatted)
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

    private fun initBarStartDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val adjustedMonth = month + 1
            barStartDate.text = makeDateString(dayOfMonth, adjustedMonth, year)
        }
        val cal = Calendar.getInstance()
        val style = AlertDialog.THEME_HOLO_LIGHT
        barStartPickerDialog = DatePickerDialog(requireContext(), style, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }

    private fun initBarEndDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val adjustedMonth = month + 1
            barEndDate.text = makeDateString(dayOfMonth, adjustedMonth, year)
        }
        val cal = Calendar.getInstance()
        val style = AlertDialog.THEME_HOLO_LIGHT
        barEndPickerDialog = DatePickerDialog(requireContext(), style, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
    }
    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
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

    fun openBarStartDatePicker(view: View) {
        if (!::barStartPickerDialog.isInitialized) {
            initBarStartDatePicker()
        }
        barStartPickerDialog.show()
    }

    fun openBarEndDatePicker(view: View) {
        if (!::barEndPickerDialog.isInitialized) {
            initBarEndDatePicker()
        }
        barEndPickerDialog.show()
    }
}