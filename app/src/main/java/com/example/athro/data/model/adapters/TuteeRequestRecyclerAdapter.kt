package com.example.athro.data.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.athro.R
import com.example.athro.data.model.User
import com.example.athro.ui.main.tutees.TuteesFragment
import com.example.athro.ui.main.tutees.TuteesViewModel
import com.google.firebase.database.DatabaseReference

class TuteeRequestRecyclerAdapter(
    val fragment: TuteesFragment,
    val database: DatabaseReference,
    val progUser: User,
    val noRequests: TextView
) : RecyclerView.Adapter<TuteeRequestRecyclerAdapter.ViewHolder>() {
    var requests =
        ViewModelProvider(fragment.requireActivity()).get(TuteesViewModel::class.java).requests.value!!

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.tutee_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tutee.text = requests[position].name
        holder.year.text =
            String.format(fragment.getString(R.string.year_what), requests[position].year)
        holder.ratingBar.rating = requests[position].tutee!!.rating
        holder.reviews.text =
            if (requests[position].tutee!!.reviews.size == 1) fragment.getString(R.string.one_review)
            else String.format(
                fragment.getString(R.string.multiple_reviews),
                requests[position].tutee!!.reviews.size
            )

        var subjectsText = ""
        requests[position].tutee!!.subjects.forEach { subject ->
            subjectsText += subject.getName(fragment) + fragment.getString(R.string.comma_space)
        }
        if (subjectsText.isEmpty())
            holder.subjectsTextView.text = ""
        else
            holder.subjectsTextView.text =
                subjectsText.removeSuffix(fragment.getString(R.string.comma_space))

        holder.acceptButton.setOnClickListener {
            database.child("users")
                .child(requests[position].email!!.substring(0, 8)).get().addOnSuccessListener {
                    progUser.tutor!!.requests.remove(requests[position].email!!)
                    progUser.tutor!!.tutees.add(requests[position].email!!)
                    database.child("users").child(progUser.email!!.substring(0, 8))
                        .setValue(progUser)

                    requests[position].tutee!!.tutors.add(progUser.email)
                    database.child("users").child(requests[position].email!!.substring(0, 8))
                        .setValue(requests[position])

                    Toast.makeText(
                        fragment.context,
                        String.format(
                            fragment.getString(R.string.accept_as_tutee),
                            requests[position].name
                        ),
                        Toast.LENGTH_SHORT
                    ).show()

                    requests.remove(requests[position])
                    notifyDataSetChanged()
                    if (requests.isEmpty()) {
                        noRequests.visibility = View.VISIBLE
                    }
                }
        }

        holder.rejectButton.setOnClickListener {
            progUser.tutor!!.requests.remove(requests[position].email!!)
            database.child("users").child(progUser.email!!.substring(0, 8))
                .setValue(progUser)

            Toast.makeText(
                fragment.context,
                String.format(
                    fragment.getString(R.string.reject_as_tutee),
                    requests[position].name
                ),
                Toast.LENGTH_SHORT
            ).show()

            requests.remove(requests[position])
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tutee: TextView = itemView.findViewById(R.id.tutee)
        val year: TextView = itemView.findViewById(R.id.year)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val reviews: TextView = itemView.findViewById(R.id.reviews)
        val subjectsTextView: TextView = itemView.findViewById(R.id.subjects)
        val acceptButton: Button = itemView.findViewById(R.id.accept_button)
        val rejectButton: Button = itemView.findViewById(R.id.reject_button)
    }
}