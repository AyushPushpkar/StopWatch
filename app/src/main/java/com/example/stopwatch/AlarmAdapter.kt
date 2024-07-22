package com.example.stopwatch

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.stopwatch.databinding.ItemalarmBinding
import java.util.Locale

class AlarmAdapter (
    private val alarms: List<Alarm>,
    private val onSwitchChanged: (Int, Boolean) -> Unit,
    private val context: Context,
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(val binding: ItemalarmBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = ItemalarmBinding.inflate(LayoutInflater.from(context), parent, false)
        return AlarmViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        val hourFormatted = if (alarm.hour > 12) alarm.hour - 12 else alarm.hour
        val period = if (alarm.hour >= 12) "PM" else "AM"

        // Specify Locale.ROOT to avoid locale-specific issues
        holder.binding.alarmTime.text = String.format(Locale.ROOT, "%02d:%02d %s", hourFormatted, alarm.minute, period)
        holder.binding.alarmSwitch.isChecked = alarm.isActive

        holder.binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            onSwitchChanged(alarm.id, isChecked)

            // Show Toast message when the switch is toggled
            val toastMessage = if (isChecked) {
                "Alarm enabled for ${String.format("%02d:%02d", alarm.hour, alarm.minute)}"
            } else {
                "Alarm disabled for ${String.format("%02d:%02d", alarm.hour, alarm.minute)}"
            }
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        }

    }
}