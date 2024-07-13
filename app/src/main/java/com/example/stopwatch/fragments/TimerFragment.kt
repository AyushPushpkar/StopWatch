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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.stopwatch.R
import com.example.stopwatch.databinding.DialogBinding
import com.example.stopwatch.databinding.FragmentStopwatchBinding
import com.example.stopwatch.databinding.FragmentTimerBinding
import java.util.concurrent.TimeUnit

class TimerFragment : Fragment() {

    private lateinit var binding: FragmentTimerBinding
    private var minutes : String? = "00"
    private var isRunning = false
    private var initialBaseTime: Long = 0
    private var pausedTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 50L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTimerBinding.inflate(inflater, container, false)

        binding.clockwhitebtn.setOnClickListener {
            showSetTimeDialog()
        }

        binding.pausebtn.setOnClickListener {
            if (!isRunning) {
                startStopwatch()
            } else {
                pauseStopwatch()
            }
        }
        binding.resetbtn.setOnClickListener {
            resetStopwatch()
        }

        return binding.root
    }

    private fun startStopwatch() {
        if (minutes == "00" || minutes == null) {
            Toast.makeText(requireContext(), "Please set a valid time before running", Toast.LENGTH_SHORT).show()
            return
        }

        isRunning = true
        // Start with selected time
        if (pausedTime == 0L) {
            initialBaseTime = SystemClock.elapsedRealtime() + minutes!!.toInt() * 60 * 1000L
        } else {
            initialBaseTime = SystemClock.elapsedRealtime() - pausedTime
        }
        handler.post(updateChronometer)

        binding.pausebtn.text = "Pause"
    }

    private val updateChronometer: Runnable = object : Runnable {
        override fun run() {
            val elapsedRealtime = SystemClock.elapsedRealtime() - initialBaseTime
            val minutes = - TimeUnit.MILLISECONDS.toMinutes(elapsedRealtime)
            val seconds =  -TimeUnit.MILLISECONDS.toSeconds(elapsedRealtime) % 60
            val milliseconds = -(elapsedRealtime % 1000) / 10
            binding.chronometer2.text = String.format("%02d:%02d:%02d", minutes, seconds, milliseconds)

            if (minutes <= 0 && seconds <= 0 && milliseconds <= 0) {
                resetStopwatch()
            } else if (isRunning) {
                handler.postDelayed(this, updateInterval)
            }
        }
    }

    private fun pauseStopwatch() {
        isRunning = false
        pausedTime = SystemClock.elapsedRealtime() - initialBaseTime
        handler.removeCallbacks(updateChronometer)
        binding.pausebtn.text = " Run "
    }

    private fun resetStopwatch() {
        isRunning = false
        pausedTime = 0
        initialBaseTime = 0
        minutes = "00"
        handler.removeCallbacks(updateChronometer)
        binding.chronometer2.text = "00:00:00"
        binding.pausebtn.text = " Run "
        binding.clocktime.text = "00:00:00"

    }

    private fun showSetTimeDialog() : AlertDialog {
        val dialogBinding = DialogBinding.inflate(LayoutInflater.from(context))

        val numberPicker = dialogBinding.numberPicker
        numberPicker.minValue =1
        numberPicker.maxValue = 60

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.btnback)

        dialogBinding.settimebtn.setOnClickListener {
            minutes = numberPicker.value.toString()
            binding.clocktime.text = numberPicker.value.toString() + " mins"
            dialog.dismiss()

        }

        dialog.show()
        return dialog

    }

}