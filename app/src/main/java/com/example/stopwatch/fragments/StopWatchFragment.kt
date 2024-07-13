package com.example.stopwatch.fragments

import android.os.Bundle
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

class StopWatchFragment : Fragment() {

    private lateinit var binding: FragmentStopwatchBinding
    private var minutes : String? = "00"
    private var isRunning = false
    private var initialBaseTime: Long = 0
    private var pausedTime: Long = 0

    var lapList = ArrayList<String>()
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStopwatchBinding.inflate(inflater, container, false)

        binding.clockwhitebtn.setOnClickListener {
            showSetTimeDialog()
        }

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
        if (minutes == "00" || minutes == null || minutes == "0") {
            // Start from zero and count upwards
            binding.chronometer2.base = SystemClock.elapsedRealtime()
            binding.chronometer2.start()

            binding.chronometer2.format = "%S %S "
            binding.textView3.text = "Stop"

            binding.chronometer2.setOnChronometerTickListener {
                val elapsedtime = binding.chronometer2.base - SystemClock.elapsedRealtime()
                if (elapsedtime >= 0) {
                    binding.chronometer2.stop()
                    isRunning = false
                    binding.textView3.text = "Run"
                }
            }
        } else {
            // Start with selected time
            if (pausedTime == 0L) {
                initialBaseTime = SystemClock.elapsedRealtime() + minutes!!.toInt() * 60 * 1000L
                binding.chronometer2.base = initialBaseTime
            } else {
                initialBaseTime = SystemClock.elapsedRealtime() - pausedTime
                binding.chronometer2.base = initialBaseTime
            }
            binding.chronometer2.start()

            binding.chronometer2.format = "%S %S "
            binding.textView3.text = "Stop"

            binding.chronometer2.setOnChronometerTickListener {
                val elapsedtime = binding.chronometer2.base - SystemClock.elapsedRealtime()
                if (elapsedtime <= 0) {
                    binding.chronometer2.stop()
                    isRunning = false
                    binding.textView3.text = "Run"
                }
            }
        }
    }

    private fun pauseStopwatch() {
        val elapsedtime = binding.chronometer2.base - SystemClock.elapsedRealtime()
        if (elapsedtime >= 0) {
            binding.chronometer2.stop()
            pausedTime = SystemClock.elapsedRealtime() - binding.chronometer2.base
            isRunning = false
            binding.textView3.text = "Run"
        }else{
            binding.chronometer2.stop()
            pausedTime = binding.chronometer2.base
            isRunning = false
            binding.textView3.text = "Run"
        }
    }

    private fun resetStopwatch() {
        binding.chronometer2.stop()
        isRunning = false
        binding.textView3.text = "Run"
        pausedTime = 0
        binding.chronometer2.base = SystemClock.elapsedRealtime()

        lapList.clear()
        arrayAdapter.notifyDataSetChanged()
    }

    private fun showSetTimeDialog() : AlertDialog {
        val dialogBinding = DialogBinding.inflate(LayoutInflater.from(context))

        val numberPicker = dialogBinding.numberPicker
        numberPicker.minValue =0
        numberPicker.maxValue = 9

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
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