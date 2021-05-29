package com.example.athro.data.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.athro.R
import com.example.athro.data.model.Log
import com.example.athro.ui.main.history.HistoryFragment

class LogRecyclerAdapter(val fragment: HistoryFragment) :
    RecyclerView.Adapter<LogRecyclerAdapter.ViewHolder>() {
    var logs = ArrayList<Log>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.log_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.date.text = logs[position].date
        holder.description.text =
            String.format(
                fragment.getString(R.string.tutored_hours_for),
                logs[position].tuteeName,
                String.format("%.1f", logs[position].hours)
            )
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val description: TextView = itemView.findViewById(R.id.description)
    }
}
