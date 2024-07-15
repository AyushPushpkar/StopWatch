package com.example.stopwatch.fragments

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.example.stopwatch.MainActivity
import com.example.stopwatch.MyNotificationManager
import com.example.stopwatch.R
import com.example.stopwatch.databinding.DialogBinding
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

        binding.pausebtn.apply {
            text = "Pause"
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 18f) // Change the text size here as needed
        }
    }

    private val updateChronometer: Runnable = object : Runnable {
        override fun run() {
            val elapsedRealtime = SystemClock.elapsedRealtime() - initialBaseTime
            val minutes = - TimeUnit.MILLISECONDS.toMinutes(elapsedRealtime)
            val seconds =  -TimeUnit.MILLISECONDS.toSeconds(elapsedRealtime) % 60
            val milliseconds = -(elapsedRealtime % 1000) / 10
            binding.chronometer2.text = String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)

            if (minutes <= 0 && seconds <= 0 && milliseconds <= 0) {
                resetStopwatch()
                showNotification()

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
        binding.chronometer2.text = "00:00.00"
        binding.pausebtn.text = " Run "
        binding.clocktime.text = "00:00:00"

    }

    private fun showNotification() {
        // Check if the fragment is attached to a context
        if (!isAdded || requireContext() == null) {
            return
        }

        val notificationIntent = Intent(requireContext(), MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(requireContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(requireContext(), MyNotificationManager().CHANNEL_ID1)
            .setContentTitle("Timer")
            .setContentText("Time's Up !")
            .setSmallIcon(R.drawable.timerred)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setColor(Color.RED)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .build()

        val manager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)

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