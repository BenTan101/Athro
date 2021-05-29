package com.example.athro.data.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.athro.R
import com.example.athro.data.model.Review
import com.example.athro.data.model.User
import com.example.athro.ui.main.profile.ProfileFragment
import com.google.firebase.database.DatabaseReference

class ProfileTuteeRecyclerAdapter(
    val fragment: ProfileFragment,
    val database: DatabaseReference,
    val progUser: User
) :
    RecyclerView.Adapter<ProfileTuteeRecyclerAdapter.ViewHolder>() {
    var teachers = ArrayList<User>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_in_profile_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.user.text = teachers[position].name
        holder.year.text =
            String.format(fragment.getString(R.string.year_what), teachers[position].year)
        holder.reviewButton.setOnClickListener {
            holder.reviewButton.visibility = View.GONE
            holder.ratingBar.visibility = View.VISIBLE
            holder.editText.visibility = View.VISIBLE
            holder.submitButton.visibility = View.VISIBLE
            holder.cancelButton.visibility = View.VISIBLE
        }

        holder.submitButton.setOnClickListener {
            teachers[position].tutor!!.reviews.add(
                Review(
                    User(progUser.name!!, progUser.email!!, progUser.year),
                    holder.ratingBar.rating,
                    holder.editText.text.toString()
                )
            )
            database.child("users").child(teachers[position].email!!.substring(0, 8))
                .setValue(teachers[position])

            Toast.makeText(
                fragment.context,
                String.format(fragment.getString(R.string.reviewed_who), teachers[position].name),
                Toast.LENGTH_SHORT
            )
                .show()

            holder.ratingBar.rating = 5F
            holder.editText.text.clear()
            holder.reviewButton.visibility = View.VISIBLE
            holder.ratingBar.visibility = View.GONE
            holder.editText.visibility = View.GONE
            holder.submitButton.visibility = View.GONE
            holder.cancelButton.visibility = View.GONE
        }

        holder.cancelButton.setOnClickListener {
            holder.ratingBar.rating = 5F
            holder.editText.text.clear()
            holder.reviewButton.visibility = View.VISIBLE
            holder.ratingBar.visibility = View.GONE
            holder.editText.visibility = View.GONE
            holder.submitButton.visibility = View.GONE
            holder.cancelButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return teachers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user: TextView = itemView.findViewById(R.id.user)
        val year: TextView = itemView.findViewById(R.id.year)

        val reviewButton: Button = itemView.findViewById(R.id.review_button)

        val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)
        val editText: EditText = itemView.findViewById(R.id.edit_text)
        val submitButton: Button = itemView.findViewById(R.id.submit_button)
        val cancelButton: Button = itemView.findViewById(R.id.cancel_button)

        init {
            ratingBar.visibility = View.GONE
            editText.visibility = View.GONE
            submitButton.visibility = View.GONE
            cancelButton.visibility = View.GONE
        }
    }
}