package com.example.stopwatch

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.stopwatch.databinding.ItemalarmBinding
import java.util.Locale

class AlarmAdapter(
    private val alarms: List<Alarm>,
    private val onDeleteClick: (Alarm) -> Unit,
    private val onToggleClick: (Alarm) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(val binding: ItemalarmBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemalarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.binding.alarmTime.text = formatTime(alarm.hour, alarm.minute)
        holder.binding.switchActive.setOnCheckedChangeListener(null) // Unset the listener first
        holder.binding.switchActive.isChecked = alarm.isActive
        holder.binding.switchActive.setOnCheckedChangeListener { _, isChecked ->
            alarm.isActive = isChecked
            // Use Handler to avoid IllegalStateException
            Handler(Looper.getMainLooper()).post {
                onToggleClick(alarm)
            }
        }
        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(alarm)
        }
    }

    override fun getItemCount() = alarms.size

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val formattedHour = if (hour % 12 == 0) 12 else hour % 12
        return String.format("%02d:%02d %s", formattedHour, minute, amPm)
    }
}
