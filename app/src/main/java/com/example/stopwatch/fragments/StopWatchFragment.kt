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
import com.example.stopwatch.R
import com.example.stopwatch.databinding.DialogBinding
import com.example.stopwatch.databinding.FragmentStopwatchBinding
import java.util.concurrent.TimeUnit

class StopWatchFragment : Fragment() {

    private lateinit var binding: FragmentStopwatchBinding
    private var isRunning = false
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var handler = Handler(Looper.getMainLooper())
    private val updateInterval = 50L

    var lapList = ArrayList<String>()
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStopwatchBinding.inflate(inflater, container, false)


        arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, lapList)
        binding.listView.adapter = arrayAdapter


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
            if(isRunning){
                lapList.add(binding.chronometer2.text.toString())
                arrayAdapter.notifyDataSetChanged()
            }
        }


        return binding.root

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

        lapList.clear()
        arrayAdapter.notifyDataSetChanged()
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