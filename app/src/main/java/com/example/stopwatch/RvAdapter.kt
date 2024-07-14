package com.example.stopwatch

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stopwatch.databinding.RvItemBinding

class RvAdapter(
    private val context: Context,
    private val totalTime : List<String>,
    private val lapTime : List<String> ,
    private val lapNo : List<String>
) : RecyclerView.Adapter<RvAdapter.lapViewHolder>() {

    inner class lapViewHolder(val binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): lapViewHolder {
        val view = RvItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return lapViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lapNo.size
    }

    override fun onBindViewHolder(holder: lapViewHolder, position: Int) {
        with(holder.binding) {
            tvlapno.text = lapNo[position]
            tvlap.text = lapTime[position]
            tvcombine.text = totalTime[position]
        }
    }
}