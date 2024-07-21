package com.example.stopwatch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.stopwatch.R
import com.example.stopwatch.databinding.FragmentAlarmBinding


class AlarmFragment : Fragment() {

    private lateinit var binding : FragmentAlarmBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlarmBinding.inflate(inflater,container,false)


        return binding.root
    }

}