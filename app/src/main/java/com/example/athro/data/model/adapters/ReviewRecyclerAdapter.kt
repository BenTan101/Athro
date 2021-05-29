package com.example.athro.data.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.athro.R
import com.example.athro.data.model.Review

class ReviewRecyclerAdapter : RecyclerView.Adapter<ReviewRecyclerAdapter.ViewHolder>() {
    var reviews = ArrayList<Review>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.review_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.reviewer.text = reviews[position].reviewer.name
        holder.ratingBar.rating = reviews[position].rating
        holder.text.text = reviews[position].text
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reviewer: TextView = itemView.findViewById(R.id.reviewer)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val text: TextView = itemView.findViewById(R.id.text)
    }
}