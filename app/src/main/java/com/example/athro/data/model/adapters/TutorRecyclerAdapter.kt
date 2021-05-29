package com.example.athro.data.model.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.athro.R
import com.example.athro.data.model.User
import com.example.athro.ui.main.tutors.TutorsFragment
import com.google.firebase.database.DatabaseReference
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class TutorRecyclerAdapter(
    val fragment: TutorsFragment,
    val database: DatabaseReference,
    val progUser: User
) :
    RecyclerView.Adapter<TutorRecyclerAdapter.ViewHolder>() {
    var tutors = ArrayList<User>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.tutor_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tutor.text = tutors[position].name
        holder.year.text =
            String.format(fragment.getString(R.string.year_what), tutors[position].year)
        holder.ratingBar.rating = tutors[position].tutor!!.rating
        holder.reviews.text =
            if (tutors[position].tutor!!.reviews.size == 1) fragment.getString(R.string.one_review)
            else String.format(
                fragment.getString(R.string.multiple_reviews),
                tutors[position].tutor!!.reviews.size
            )

        var subjectsText = ""
        tutors[position].tutor!!.subjects.forEach { subject ->
            subjectsText += subject.getName(fragment) + fragment.getString(R.string.comma_space)
        }
        if (subjectsText.isEmpty())
            holder.subjectsTextView.text = ""
        else {
            holder.subjectsTextView.text =
                subjectsText.removeSuffix(fragment.getString(R.string.comma_space))
        }

        holder.requestButton.setOnClickListener {
            database.child("users")
                .child(tutors[position].email!!.substring(0, 8)).get().addOnSuccessListener {
                    val tutor = User(JSONObject(it.value as HashMap<*, *>))
                    if (tutor.tutor!!.requests.contains(progUser.email!!)) {
                        Toast.makeText(
                            fragment.context,
                            String.format(
                                fragment.getString(R.string.request_already_made),
                                tutors[position].name
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (tutor.tutor!!.tutees.contains(progUser.email)) {
                        Toast.makeText(
                            fragment.context,
                            String.format(
                                fragment.getString(R.string.already_tutee),
                                tutors[position].name
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        tutors[position].tutor!!.requests.add(progUser.email)
                        database.child("users").child(tutors[position].email!!.substring(0, 8))
                            .setValue(tutors[position])
                        Toast.makeText(
                            fragment.context,
                            String.format(
                                fragment.getString(R.string.request_made),
                                tutors[position].name
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        holder.contactButton.setOnClickListener {
            val addresses = arrayOf(tutors[position].email)
            var title: String
            var text: String
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                type = "text/html"
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, addresses)

                title = fragment.getString(R.string.tutorage_enquiry)
                putExtra(Intent.EXTRA_SUBJECT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            try {
                fragment.startActivity(
                    Intent.createChooser(
                        emailIntent,
                        String.format(
                            fragment.getString(R.string.contacting_who),
                            tutors[position].name
                        ),
                    )
                )
            } catch (e: android.content.ActivityNotFoundException) {
                Toast.makeText(
                    fragment.context,
                    fragment.getString(R.string.no_email_client),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return tutors.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tutor: TextView = itemView.findViewById(R.id.user)
        val year: TextView = itemView.findViewById(R.id.year)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val reviews: TextView = itemView.findViewById(R.id.reviews)
        val subjectsTextView: TextView = itemView.findViewById(R.id.subjects)
        val requestButton: Button = itemView.findViewById(R.id.request_button)
        val contactButton: Button = itemView.findViewById(R.id.contact_button)
    }
}