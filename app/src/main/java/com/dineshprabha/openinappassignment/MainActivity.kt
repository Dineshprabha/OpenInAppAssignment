package com.dineshprabha.openinappassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dineshprabha.openinappassignment.databinding.ActivityMainBinding
import com.dineshprabha.openinappassignment.ui.home.CampaignFragment
import com.dineshprabha.openinappassignment.ui.home.CourseFragment
import com.dineshprabha.openinappassignment.ui.home.LinksFragment
import com.dineshprabha.openinappassignment.ui.home.ProfileFragment
import com.dineshprabha.openinappassignment.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.main_fragment)
        binding.bottomNavigation.setupWithNavController(navController)


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.getOrCreateBadge(R.id.dashboardFragment).apply {
            backgroundColor = resources.getColor(R.color.g_blue)
        }
    }


}