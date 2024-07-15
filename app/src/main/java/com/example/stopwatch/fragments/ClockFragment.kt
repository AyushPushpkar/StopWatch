package com.example.stopwatch.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.stopwatch.databinding.FragmentClockBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClockFragment : Fragment() {

    private lateinit var binding: FragmentClockBinding
    private lateinit var timeFormat : SimpleDateFormat
    private var  handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentClockBinding.inflate(inflater, container, false)

        val digitalFont = Typeface.createFromAsset(requireContext().assets, "fonts/digital.ttf")

//        binding.date.typeface = digitalFont
//        binding.format.typeface = digitalFont
        binding.digitalclock.typeface = digitalFont

        timeFormat = SimpleDateFormat("HH:mm:ss" , Locale.getDefault())
        handler.post(object : Runnable {
            override fun run() {
                updateUI()
                handler.postDelayed(this, 1000)
            }
        })

        return binding.root
    }

    private fun updateUI() {
        val currenttime = System.currentTimeMillis()
        val formattime = timeFormat.format(Date(currenttime))
        var hr = formattime.substring(0,2).toInt()
        var min = formattime.substring(3,5).toInt()
        var sec = formattime.substring(6).toInt()

        rotateimagewithshift(binding.hourtick,tomap(hr,12),0.5f,1.0f)
        rotateimagewithshift(binding.mintick,tomap(min,60),0.5f,1.0f)
        rotateimagewithshift(binding.sectick,tomap(sec,60),0.5f,1.0f)
    }

    private fun rotateimagewithshift(imageView: ImageView, angle : Float, pivotxvalue : Float, pivotyvalue : Float) {

//        imageView.pivotX =  imageView.width*pivotxvalue
//        imageView.pivotY =  imageView.height*pivotyvalue

        // Convert 10dp to pixels
        val pivotOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)

        // Set pivot to the center of the custom clock plus the offset

        imageView.pivotX = imageView.width * pivotxvalue
        imageView.pivotY = (imageView.height - pivotOffset) * pivotyvalue

        // Calculate current rotation
        val currentRotation = imageView.rotation % 360

        // Calculate desired rotation
        var desiredRotation = angle
        if (desiredRotation < 0) {
            desiredRotation += 360 // Ensure positive angle for correct rotation
        }

        // Determine the shortest path to rotate (clockwise or counterclockwise)
        var rotationDirection = desiredRotation - currentRotation
        if (rotationDirection > 180) {
            rotationDirection -= 360
        } else if (rotationDirection < -180) {
            rotationDirection += 360
        }

        // Animate rotation
        imageView.animate()
            .rotationBy(rotationDirection)
            .setDuration(500) // Adjust duration as needed for smoother animation
            .start()

    }

    private fun tomap(value :Int , max : Int) : Float{
        return (value%max).toFloat()/max*360f
    }


}