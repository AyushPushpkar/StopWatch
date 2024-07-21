package com.example.stopwatch.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.example.stopwatch.databinding.FragmentClockBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClockFragment : Fragment() {

    private lateinit var binding: FragmentClockBinding
    private lateinit var timeFormat : SimpleDateFormat
    private var  handler = Handler()
    private var isInitialUpdate = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentClockBinding.inflate(inflater, container, false)

        val digitalFont = Typeface.createFromAsset(requireContext().assets, "fonts/digital.ttf")
        binding.digitalclock.typeface = digitalFont


        timeFormat = SimpleDateFormat("HH:mm:ss" , Locale.getDefault())

        // Set up a layout listener to ensure the view is fully created before updating UI
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to prevent multiple calls
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                // Perform initial UI update to set hands' positions
                updateUI()
                // Start the clock update handler
                startClockUpdate()
            }
        })

        return binding.root
    }

    private fun startClockUpdate(){
        handler.post(object : Runnable {
            override fun run() {
                if (isAdded) { // Check if fragment is attached
                    updateUI()
                    handler.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun updateUI() {
        val currenttime = System.currentTimeMillis()
        val formattime = timeFormat.format(Date(currenttime))
        var hr = formattime.substring(0,2).toInt()
        var min = formattime.substring(3,5).toInt()
        var sec = formattime.substring(6).toInt()

        // Calculate the angles for hour, minute, and second hands
        val hourAngle = ((hr % 12) + min / 60f + sec / 3600f) * 30f // 360/12 = 30 degrees per hour
        val minuteAngle = (min + sec / 60f) * 6f // 360/60 = 6 degrees per minute
        val secondAngle = sec * 6f // 360/60 = 6 degrees per second

        rotateimagewithshift(binding.hourtick,hourAngle,0.5f,1.0f , !isInitialUpdate)
        rotateimagewithshift(binding.mintick,minuteAngle,0.5f,1.0f , !isInitialUpdate)
        rotateimagewithshift(binding.sectick,secondAngle,0.5f,1.0f , !isInitialUpdate)

        isInitialUpdate = false
    }

    private fun rotateimagewithshift(imageView: ImageView, angle : Float, pivotxvalue : Float, pivotyvalue : Float , animate : Boolean) {

        viewLifecycleOwner.lifecycleScope.launch {
            if (isAdded) { // Check if fragment is added to the activity
                // Convert 10dp to pixels
                val pivotOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
                imageView.pivotX = imageView.width * pivotxvalue
                imageView.pivotY = (imageView.height - pivotOffset) * pivotyvalue


                if (animate) {
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
                        .setInterpolator(LinearInterpolator()) // Use linear interpolator for smooth animation
                        .setDuration(200) // Adjust duration to be closer to 1 second for smoother updates
                        .start()
                } else {
                    imageView.rotation = angle
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Reset the flag when the view is destroyed
        isInitialUpdate = true
    }


}