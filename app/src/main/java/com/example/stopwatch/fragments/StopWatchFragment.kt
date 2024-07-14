package com.example.stopwatch.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stopwatch.R
import com.example.stopwatch.RvAdapter
import com.example.stopwatch.databinding.DialogBinding
import com.example.stopwatch.databinding.FragmentStopwatchBinding
import java.util.concurrent.TimeUnit

class StopWatchFragment : Fragment() {

    private lateinit var binding: FragmentStopwatchBinding
    private var isRunning = false
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var lastLapTime: Long = 0
    private var handler = Handler(Looper.getMainLooper())
    private val updateInterval = 50L

    private var lapNoList = ArrayList<String>()
    private var lapTimeList = ArrayList<String>()
    private var totalTimeList = ArrayList<String>()
    private lateinit var lapAdapter: RvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStopwatchBinding.inflate(inflater, container, false)

        lapAdapter = RvAdapter(requireContext(), totalTimeList, lapTimeList, lapNoList)
        binding.recview.layoutManager = LinearLayoutManager(requireContext())
        binding.recview.adapter = lapAdapter
        binding.recview.visibility = View.GONE

        binding.runbtn.setOnClickListener {
            if (!isRunning) {
                startStopwatch()
            } else {
                pauseStopwatch()
            }
        }
        binding.resetbtn.setOnClickListener {
            resetStopwatch()
        }
        binding.lapbtn.setOnClickListener {
            if (isRunning) {
                recordLap()
            }
        }


        return binding.root

    }

    private fun recordLap() {
        val currentTime = SystemClock.elapsedRealtime()
        val totalTime = currentTime - startTime

        val lapTime = if (lapNoList.isEmpty()) {
            totalTime
        } else {
            currentTime - lastLapTime
        }

        lapNoList.add(0, "Lap ${lapNoList.size + 1}")
        lapTimeList.add(0, formatTime(lapTime))
        totalTimeList.add(0, formatTime(totalTime))
        lastLapTime = currentTime

        lapAdapter.notifyDataSetChanged()

        // Set RecyclerView visibility to VISIBLE when a lap is recorded
        binding.recview.visibility = View.VISIBLE
    }

    private fun startStopwatch() {
        isRunning = true
        startTime = SystemClock.elapsedRealtime() - elapsedTime
        handler.postDelayed(runnable, updateInterval)
        binding.textView3.text = "Pause"
    }

    private fun pauseStopwatch() {
        isRunning = false
        handler.removeCallbacks(runnable)
        binding.textView3.text = "Resume"
    }

    private fun resetStopwatch() {
        isRunning = false
        handler.removeCallbacks(runnable)
        binding.chronometer2.text = "00:00.00"
        elapsedTime = 0
        binding.textView3.text = "Run"

        lapNoList.clear()
        lapTimeList.clear()
        totalTimeList.clear()
        lapAdapter.notifyDataSetChanged()

        binding.recview.visibility =  View.GONE
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            binding.chronometer2.text = formatTime(elapsedTime)
            handler.postDelayed(this, updateInterval)
        }
    }

    private fun formatTime(time: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60
        val milliseconds = time % 1000 / 10
        return String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
    }

}