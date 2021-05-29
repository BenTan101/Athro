package com.example.athro.data.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.athro.R
import com.example.athro.data.model.Log
import com.example.athro.data.model.User
import com.example.athro.ui.main.log.LogFragment
import com.google.android.material.slider.Slider
import com.google.firebase.database.DatabaseReference
import java.util.*
import kotlin.collections.ArrayList

class LogTuteeRecyclerAdapter(
    val fragment: LogFragment,
    val database: DatabaseReference,
    val progUser: User
) :
    RecyclerView.Adapter<LogTuteeRecyclerAdapter.ViewHolder>() {
    var tutees = ArrayList<User>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.log_tutee_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tutee.text = tutees[position].name
        holder.year.text =
            String.format(fragment.getString(R.string.year_what, tutees[position].year))
        holder.logButton.setOnClickListener {
            holder.logLayout.visibility = View.VISIBLE
        }

        holder.slider.setLabelFormatter { value: Float ->
            String.format("%.1f", value)
        }

        holder.slider.addOnChangeListener { slider: Slider, fl: Float, b: Boolean ->
            if (holder.slider.value == 0F) {
                holder.slider.value = 0.5F
            }
        }

        holder.submitButton.setOnClickListener {
            progUser.tutor!!.logs.add(
                Log(
                    tutees[position].name!!,
                    holder.slider.value.toDouble(),
                    Date()
                )
            )
            database.child("users").child(progUser.email!!.substring(0, 8)).setValue(progUser)

            Toast.makeText(
                fragment.context,
                String.format(
                    fragment.getString(R.string.logged_hours_for),
                    String.format("%.1f", holder.slider.value),
                    tutees[position].name!!
                ),
                Toast.LENGTH_SHORT
            ).show()

            holder.slider.value = 0.5F
            holder.logLayout.visibility = View.GONE
        }

        holder.cancelButton.setOnClickListener {
            holder.slider.value = 0.5F
            holder.logLayout.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return tutees.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tutee: TextView = itemView.findViewById(R.id.tutee)
        val year: TextView = itemView.findViewById(R.id.year)
        val logButton: Button = itemView.findViewById(R.id.log_button)

        val logLayout: LinearLayout = itemView.findViewById(R.id.log_layout)
        val slider: Slider = itemView.findViewById(R.id.slider)
        val submitButton: Button = itemView.findViewById(R.id.submit_button)
        val cancelButton: Button = itemView.findViewById(R.id.cancel_button)

        init {
            logLayout.visibility = View.GONE
        }
    }
}