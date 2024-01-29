package com.dineshprabha.openinappassignment.ui.fragments

import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dineshprabha.openinappassignment.R
import com.dineshprabha.openinappassignment.adapter.DashboardItemAdapter
import com.dineshprabha.openinappassignment.adapter.TabLayoutAdapter
import com.dineshprabha.openinappassignment.api.APIService
import com.dineshprabha.openinappassignment.databinding.FragmentDashboardBinding
import com.dineshprabha.openinappassignment.models.DashboardItem
import com.dineshprabha.openinappassignment.models.OverallUrlChart
import com.dineshprabha.openinappassignment.models.UserClickResponse
import com.dineshprabha.openinappassignment.utils.Constants.BEARER_TOKEN
import com.dineshprabha.openinappassignment.utils.DateAxisValueFormatter
import com.dineshprabha.openinappassignment.utils.NetworkResult
import com.dineshprabha.openinappassignment.utils.TokenManager
import com.dineshprabha.openinappassignment.viewmodel.MainViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    private val viewModel by viewModels<MainViewModel>()
    private val dashboardItemAdapter by lazy { DashboardItemAdapter() }
    private val dashboardItemList = ArrayList<DashboardItem>()
    private lateinit var chartData: Map<String, Int>

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tokenManager.saveToken(BEARER_TOKEN)

        val greetingMessage = getGreetingMessage()
        binding.tvGreetings.text = getGreetingMessage()

        viewModel.fetchData()
        setupDashboardItemRV()


        val categoriesFragment = arrayListOf(
            TopLinksFragment(),
            RecentLinksFragment()
        )

        binding.viewPager2.isUserInputEnabled = false
        val viewPagerAdapter = TabLayoutAdapter(categoriesFragment, childFragmentManager, lifecycle)
        binding.viewPager2.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "Top Links"
                1 -> tab.text = "Recent Links"
            }
        }.attach()
        setupObserver()

    }

    private fun getGreetingMessage(): String {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        return when {
            hourOfDay < 12 -> "Good Morning!"
            hourOfDay < 18 -> "Good Afternoon!"
            else -> "Good Evening!"
        }
    }


    private fun setupDashboardItemRV() {
        binding.dashboardRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = dashboardItemAdapter
        }
    }

    private fun setupObserver() {
        viewModel.userClickLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    if(it.data != null){
                        val userClickResponse :UserClickResponse = it.data
                        setupDashboardItemData(userClickResponse)
                        val overallUrlChart : OverallUrlChart = userClickResponse.data.overall_url_chart
                        chartData = extractChartData(overallUrlChart)
                        populatelineChart(chartData)
                    }
                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is NetworkResult.Loading -> {

                }
                else -> Unit
            }
        })
    }

    private fun populatelineChart(chartData: Map<String, Int>) {

        // Extract data from your JSON-like structure
        val data = chartData

        // Create entries for the chart
        val entries = ArrayList<Entry>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        for ((date, value) in data) {
            calendar.time = dateFormat.parse(date)!!

            // Convert date to timestamp for X-axis
            val timestamp = calendar.timeInMillis.toFloat()
            entries.add(Entry(timestamp, value.toFloat()))
        }

        // Create a dataset with entries
        val dataSet = LineDataSet(entries, "Overall URL Chart")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK

        // Create a LineData object with the dataset
        val lineData = LineData(dataSet)

        val lineChart = binding.lineChart
        // Set up X-axis with date labels
        val xAxis: XAxis = lineChart.xAxis
        xAxis.valueFormatter = DateAxisValueFormatter()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f

        // Set up chart properties
        lineChart.data = lineData
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.invalidate()

    }

    fun extractChartData(overallUrlChart: OverallUrlChart): Map<String, Int> {
        val properties = OverallUrlChart::class.java.declaredFields
        return properties.associate {
            it.isAccessible = true
            it.name to it.getInt(overallUrlChart)
        }
    }

    private fun setupDashboardItemData(userClickResponse: UserClickResponse) {
        dashboardItemList.add(DashboardItem(R.drawable.avatar_two, userClickResponse.today_clicks.toString(), "Today's Clicks" ))
        dashboardItemList.add(DashboardItem(R.drawable.avathar_one, userClickResponse.top_location.toString(), "Top Location" ))
        dashboardItemList.add(DashboardItem(R.drawable.avatar_three, userClickResponse.top_source.toString(), "Top Source" ))
        dashboardItemList.add(DashboardItem(R.drawable.avathar_one, userClickResponse.startTime.toString(), "Best Time" ))
        dashboardItemAdapter.differ.submitList(dashboardItemList)
    }
}