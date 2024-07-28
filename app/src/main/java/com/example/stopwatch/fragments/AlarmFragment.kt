package com.example.stopwatch.fragments

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.audiofx.Equalizer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class AlarmFragment : Fragment() {

    private lateinit var binding: FragmentAlarmBinding
    private lateinit var calendar: Calendar
    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences("AlarmSharedPreferences", Context.MODE_PRIVATE)
    }
    private lateinit var adapter: AlarmAdapter
    private val alarms: MutableList<Alarm> = mutableListOf()
    private var isTimeSelected = false // Flag to check if time has been selected

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

        // Load the saved alarms
        alarms.addAll(loadAlarms())

        // Set up RecyclerView
        binding.recyclerViewAlarms.layoutManager = LinearLayoutManager(context)
        adapter = AlarmAdapter(alarms, { alarm -> deleteAlarm(alarm) }, { alarm -> toggleAlarm(alarm) })
        binding.recyclerViewAlarms.adapter = adapter

        // Set up listeners
        binding.imageBtnTime.setOnClickListener {
            showTimePicker()
        }

        binding.setalarmbtn.setOnClickListener {
            if (!isTimeSelected) {
                Toast.makeText(requireContext(), "Please select a time first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val alarmId = (alarms.maxByOrNull { it.id }?.id ?: 0) + 1 // Generate a new unique ID
            val alarm = Alarm(
                id = alarmId,
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE),
                isActive = true
            )
            addAlarm(alarm)
            setAlarm(alarm)
            isTimeSelected = false // Reset the flag after setting the alarm
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
            isTimeSelected = true // Set the flag to true after selecting the time
        }
    }

    private fun saveAlarms() {
        val gson = Gson()
        val json = gson.toJson(alarms)
        sharedPreferences.edit().putString("alarms", json).apply()
    }

    private fun loadAlarms(): List<Alarm> {
        alarms.clear() // Clear the existing alarms to avoid duplicates
        val gson = Gson()
        val json = sharedPreferences.getString("alarms", null)
        val type = object : TypeToken<List<Alarm>>() {}.type
        return if (json == null) {
            emptyList()
        } else {
            gson.fromJson(json, type)
        }
    }

    private fun addAlarm(alarm: Alarm) {
        alarms.add(alarm)
        alarms.sortBy { it.hour * 60 + it.minute } // Sort after adding
        saveAlarms()
        adapter.notifyItemInserted(alarms.size - 1)
    }

    private fun updateAlarm(alarm: Alarm) {
        val index = alarms.indexOfFirst { it.id == alarm.id }
        if (index != -1) {
            alarms[index] = alarm
            alarms.sortBy { it.hour * 60 + it.minute } // Sort after adding
            saveAlarms()
            // Post the update to avoid IllegalStateException
            Handler(Looper.getMainLooper()).post {
                adapter.notifyItemChanged(index)
            }
        }
    }

    private fun deleteAlarm(alarm: Alarm) {
        val index = alarms.indexOf(alarm)
        if (index != -1) {
            alarms.removeAt(index)
            saveAlarms()
            adapter.notifyItemRemoved(index)
            cancelAlarm(alarm)
            Toast.makeText(requireContext(), "Alarm Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleAlarm(alarm: Alarm) {
        if (alarm.isActive) {
            setAlarm(alarm)
        } else {
            cancelAlarm(alarm)
            Toast.makeText(requireContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show()
        }
        updateAlarm(alarm)
    }

    private fun setAlarm(alarm: Alarm) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarm.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(requireContext(), "Alarm Set", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm(alarm: Alarm) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)

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
