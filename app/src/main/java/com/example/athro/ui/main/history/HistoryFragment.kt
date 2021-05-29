package com.example.athro.ui.main.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athro.R
import com.example.athro.data.model.Log
import com.example.athro.data.model.User
import com.example.athro.data.model.adapters.LogRecyclerAdapter
import com.example.athro.databinding.FragmentHistoryBinding
import com.example.athro.ui.MainViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class HistoryFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var logRecyclerAdapter: LogRecyclerAdapter
    private lateinit var progUser: User
    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        progUser = mainViewModel.progUser.value!!

        logRecyclerAdapter = LogRecyclerAdapter(this)

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // I.e. The user is not a tutor and doesn't need to find tutees.
        if (progUser.tutor == null) {
            binding.isTutorLayout.visibility = View.GONE
            binding.notTutorLayout.visibility = View.VISIBLE
            return
        } else {
            binding.notTutorLayout.visibility = View.GONE
        }

        logRecyclerAdapter.logs =
            ArrayList(progUser.tutor!!.logs.sortedWith(compareBy { it.date }).reversed())

        if (logRecyclerAdapter.logs.isEmpty()) {
            binding.noHours.visibility = View.VISIBLE
            binding.graphTitle.visibility = View.GONE
            binding.dailyChart.visibility = View.GONE
            binding.cumulativeChart.visibility = View.GONE
            binding.dailyButton.visibility = View.GONE
            binding.cumulativeButton.visibility = View.GONE
            binding.logRecyclerView.visibility = View.GONE
            return
        }

        binding.logRecyclerView.adapter = logRecyclerAdapter
        val layoutManager = LinearLayoutManager(context)
        binding.logRecyclerView.layoutManager = layoutManager

        binding.dailyChart.description.isEnabled = false
        binding.dailyChart.legend.isEnabled = false
        binding.cumulativeChart.description.isEnabled = false
        binding.cumulativeChart.legend.isEnabled = false

        setDailyData(binding.dailyChart, progUser.tutor!!.logs)
        setCumulativeData(binding.cumulativeChart, progUser.tutor!!.logs)

        binding.cumulativeChart.visibility = View.GONE

        binding.dailyButton.setOnClickListener {
            binding.dailyButton.setTextColor(resources.getColor(R.color.black))
            binding.dailyButton.setBackgroundColor(resources.getColor(R.color.light_blue))
            binding.cumulativeButton.setTextColor(resources.getColor(R.color.white))
            binding.cumulativeButton.setBackgroundColor(resources.getColor(R.color.dark_gray))

            binding.dailyChart.visibility = View.VISIBLE
            binding.cumulativeChart.visibility = View.GONE
        }

        binding.cumulativeButton.setOnClickListener {
            binding.cumulativeButton.setTextColor(resources.getColor(R.color.black))
            binding.cumulativeButton.setBackgroundColor(resources.getColor(R.color.light_blue))
            binding.dailyButton.setTextColor(resources.getColor(R.color.white))
            binding.dailyButton.setBackgroundColor(resources.getColor(R.color.dark_gray))

            binding.dailyChart.visibility = View.GONE
            binding.cumulativeChart.visibility = View.VISIBLE
        }
    }

    private fun setDailyData(chart: LineChart, logs: ArrayList<Log>) {
        chart.data = null

        val values = java.util.ArrayList<Entry>()

        var i = 1
        var logHours: Double
        var logDate: String
        val firstDate = logs[0].date
        var sdf = SimpleDateFormat("dd/MM/yyyy")

        while (i < logs.size + 1) {
            logDate = logs[i - 1].date
            logHours = logs[i - 1].hours

            while (i < logs.size && logDate == logs[i].date) {
                logHours += logs[i].hours
                i++
            }

            values.add(
                Entry(
                    TimeUnit.DAYS.convert(
                        sdf.parse(logs[i - 1].date).time - sdf.parse(firstDate).time,
                        TimeUnit.MILLISECONDS
                    ).toFloat(),
                    logHours.toFloat()
                )
            )

            i++
        }

        val set1: LineDataSet
        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // Create a dataset and give it a type.
            set1 = LineDataSet(values, "Tutorage hours")

            formatSet(set1, chart)

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1)

            // Create a data object with the data sets.
            val data = LineData(dataSets)
            chart.data = data

            set1.valueFormatter = LabelValueFormatter(chart, logs)

            // Format axes.
            val xAxis = chart.xAxis
            xAxis.labelCount = 7
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)

            val xAxisFormatter: ValueFormatter = DayAxisValueFormatter(chart, logs)
            xAxis.valueFormatter = xAxisFormatter
            xAxis.textColor = resources.getColor(R.color.light_blue)

            val yAxis = chart.axisLeft
            yAxis.textColor = resources.getColor(R.color.light_blue)
        }
    }

    private fun setCumulativeData(chart: LineChart, logs: ArrayList<Log>) {
        chart.data = null

        val values = java.util.ArrayList<Entry>()

        var i = 1
        var logHours = 0.0
        var logDate: String
        val firstDate = logs[0].date
        var sdf = SimpleDateFormat("dd/MM/yyyy")

        while (i < logs.size + 1) {
            logDate = logs[i - 1].date
            logHours += logs[i - 1].hours

            while (i < logs.size && logDate == logs[i].date) {
                logHours += logs[i].hours
                i++
            }

            values.add(
                Entry(
                    TimeUnit.DAYS.convert(
                        sdf.parse(logs[i - 1].date).time - sdf.parse(firstDate).time,
                        TimeUnit.MILLISECONDS
                    ).toFloat(),
                    logHours.toFloat()
                )
            )

            i++
        }

        val set1: LineDataSet
        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // Create a dataset and give it a type.
            set1 = LineDataSet(values, "Tutorage hours")

            formatSet(set1, chart)

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1)

            // Create a data object with the data sets.
            val data = LineData(dataSets)

            chart.data = data

            set1.valueFormatter = LabelValueFormatter(chart, logs)

            // Format axes.
            val xAxis = chart.xAxis
            xAxis.labelCount = 7
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)

            val xAxisFormatter: ValueFormatter = DayAxisValueFormatter(chart, logs)
            xAxis.valueFormatter = xAxisFormatter
            xAxis.textColor = resources.getColor(R.color.light_blue)

            val yAxis = chart.axisLeft
            yAxis.textColor = resources.getColor(R.color.light_blue)
        }
    }

    private fun formatSet(set1: LineDataSet, chart: LineChart) {
        set1.setDrawIcons(false)

        set1.color = resources.getColor(R.color.lighter_orange)
        set1.setCircleColor(resources.getColor(R.color.lighter_orange))

        // Point labels.
        set1.valueTextColor = resources.getColor(R.color.orange)
        set1.valueTextSize = 12f

        // Line thickness and point size.
        set1.lineWidth = 1f
        set1.circleRadius = 3f

        // Draw points as solid circles.
        set1.setDrawCircleHole(false)

        set1.formLineWidth = 1f
        set1.formSize = 15f

        set1.enableDashedHighlightLine(10f, 5f, 0f)

        // Set the filled area.
        set1.setDrawFilled(true)
        set1.fillColor = resources.getColor(R.color.lighter_orange)
        set1.fillFormatter =
            IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }
    }

    class LabelValueFormatter(val chart: LineChart, val logs: ArrayList<Log>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return String.format("%.1f", value)
        }
    }

    class DayAxisValueFormatter(val chart: LineChart, val logs: ArrayList<Log>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val logDate = logs[0].date

            val c = Calendar.getInstance()
            c.time = SimpleDateFormat("dd/MM/yyyy").parse(logDate)
            c.add(Calendar.DAY_OF_MONTH, value.toInt())

            return SimpleDateFormat("dd/MM").format(c.time)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

