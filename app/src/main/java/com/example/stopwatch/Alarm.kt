package com.example.stopwatch

data class Alarm(
    val id: Int, // Unique ID for each alarm
    val hour: Int,
    val minute: Int,
    val isActive: Boolean
)

