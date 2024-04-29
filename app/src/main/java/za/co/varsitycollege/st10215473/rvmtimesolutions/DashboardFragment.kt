package za.co.varsitycollege.st10215473.rvmtimesolutions

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class DashboardFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var progressBar: ProgressBar
    private lateinit var seekBar: SeekBar
    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        pieChart = view.findViewById(R.id.pie_chart)

        val pieList: ArrayList<PieEntry> = ArrayList()
        pieList.add(PieEntry(50f, "Construction"))
        pieList.add(PieEntry(150f, "App Development"))
        pieList.add(PieEntry(23f, "Marketing"))
        pieList.add(PieEntry(53f, "Research"))

        val pieDataSet = PieDataSet(pieList, "List")
        // Define custom colors for the slices
        val colors = listOf(
            Color.parseColor("#A5BEEF"), // Construction
            Color.parseColor("#7B9BDA"), // App Development
            Color.parseColor("#3F68BA"), // Marketing
            Color.parseColor("#1645A4")  // Research
        )
        pieDataSet.colors = colors
        pieDataSet.valueTextSize=15f
        pieDataSet.valueTextColor= Color.BLACK
        val pieData = PieData(pieDataSet)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.data = pieData
        pieChart.centerText = "Total Hours Worked Per Category"
        pieChart.animateY(750)

        pieChart.setCenterTextSize(15f)
        pieChart.setCenterTextColor(Color.BLACK)
        pieChart.setCenterTextOffset(0f, 0f)
        pieChart.setCenterTextRadiusPercent(100f)
        pieChart.setDrawCenterText(true)
        pieChart.setHoleColor(Color.TRANSPARENT)

        val barChart = view.findViewById<BarChart>(R.id.bar_chart)
        setupChart(barChart)
        setData(barChart)
        return view
    }


    private fun setupChart(barChart: BarChart) {
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setPinchZoom(true)
        barChart.setDrawGridBackground(false)
        barChart.setTouchEnabled(false)
        barChart.animateY(1000)
        barChart.setDrawGridBackground(false)
    }

    private fun setData(barChart: BarChart) {
        val days = arrayOf("Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun")
        val entries1 = floatArrayOf(5f, 3f, 4f, 2f, 1f, 6f, 2f) // Adjusted entries to fit the range
        val entries2 = floatArrayOf(8f, 7f, 8f, 4f, 7f, 8f, 3f) // Adjusted entries to fit the range
        val entries3 = floatArrayOf(3f, 2f, 1f, 6f, 2f, 9f, 7f) // Adjusted entries to fit the range

        val values1 = ArrayList<BarEntry>()
        val values2 = ArrayList<BarEntry>()
        val values3 = ArrayList<BarEntry>()

        for (i in days.indices) {
            values1.add(BarEntry(i.toFloat(), entries1[i]))
            values2.add(BarEntry(i.toFloat(), entries2[i]))
            values3.add(BarEntry(i.toFloat(), entries3[i]))

        }

        val set1 = BarDataSet(values1, "DataSet 1")
        set1.color = Color.parseColor("#A5BEEF")

        val set2 = BarDataSet(values2, "DataSet 2")
        set2.color = Color.parseColor("#3F68BA")

        val set3 = BarDataSet(values3, "DataSet 3")
        set3.color = Color.parseColor("#7B9BDA")

        val data = BarData(set3, set2, set1)
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
            formColor = Color.parseColor("#7B9BDA")

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
}