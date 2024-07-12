package com.example.stopwatch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.stopwatch.databinding.ActivityMainBinding
import com.example.stopwatch.fragments.StopWatchFragment
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val stopWatchFragment = StopWatchFragment()
//        val secFragment = BlankFragment3()

        setCurrentFragment(stopWatchFragment)

        binding.bottomBar.onTabSelected = {
            when (it.id) {
                R.id.tab_stopwatch -> setCurrentFragment(stopWatchFragment)
                R.id.tab_alarm -> setCurrentFragment(stopWatchFragment)
                R.id.tab_weather -> setCurrentFragment(stopWatchFragment)
                R.id.tab_clock -> setCurrentFragment(stopWatchFragment)
                R.id.tab_timezone -> setCurrentFragment(stopWatchFragment)
            }

            // Remove the badge from the selected tab
            binding.bottomBar.clearBadgeAtTab(it)

        }

        binding.bottomBar.onTabReselected = {
            // Handle tab reselection if needed
        }

        binding.bottomBar.post {
            // Adding badges to specific tabs
            binding.bottomBar.setBadgeAtTabIndex(1, AnimatedBottomBar.Badge("99"))
            binding.bottomBar.setBadgeAtTabIndex(4, AnimatedBottomBar.Badge("5"))
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout2, fragment)
            commit()
        }

}