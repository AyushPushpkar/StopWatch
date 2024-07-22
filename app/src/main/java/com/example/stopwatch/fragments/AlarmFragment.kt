package com.example.stopwatch.fragments

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stopwatch.Alarm
import com.example.stopwatch.AlarmAdapter
import com.example.stopwatch.AlarmReceiver
import com.example.stopwatch.databinding.FragmentAlarmBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar


class AlarmFragment : Fragment() {

    private lateinit var binding: FragmentAlarmBinding
    private lateinit var calendar: Calendar
    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlarmBinding.inflate(inflater, container, false)

        // Initialize the calendar
        calendar = Calendar.getInstance()

        // Create notification channel
        createNotificationChannel()

        // Load the saved time
        loadSavedTime()

        // Set up listeners
        binding.imageBtnTime.setOnClickListener {
            showTimePicker()
        }

        binding.setalarmbtn.setOnClickListener {
            setAlarm()
        }

        binding.cancelalarmbtn.setOnClickListener {
            cancelAlarm()
        }

        return binding.root
    }

    private fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        timePicker.show(parentFragmentManager, "androidknowledge")

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val formattedTime = if (hour > 12) {
                String.format("%02d:%02d PM", hour - 12, minute)
            } else {
                String.format("%02d:%02d AM", hour, minute)
            }
            binding.selectTime.text = formattedTime

            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Save the selected time
            saveTime(hour, minute)
        }
    }

    private fun saveTime(hour: Int, minute: Int) {
        with(sharedPreferences.edit()) {
            putInt("hour", hour)
            putInt("minute", minute)
            apply()
        }
    }

    private fun loadSavedTime() {
        val hour = sharedPreferences.getInt("hour", 12)
        val minute = sharedPreferences.getInt("minute", 0)
        val formattedTime = if (hour > 12) {
            String.format("%02d:%02d PM", hour - 12, minute)
        } else {
            String.format("%02d:%02d AM", hour, minute)
        }
        binding.selectTime.text = formattedTime

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun setAlarm() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(requireContext(), "Alarm Set", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.cancel(pendingIntent)

        Toast.makeText(requireContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        val name: CharSequence = "akchannel"
        val desc = "Channel for Alarm Manager"
        val imp = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("androidknowledge", name, imp)
        channel.description = desc
        val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}
