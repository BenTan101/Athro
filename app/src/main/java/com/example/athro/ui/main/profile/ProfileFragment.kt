package com.example.athro.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athro.R
import com.example.athro.data.model.*
import com.example.athro.data.model.adapters.ProfileTuteeRecyclerAdapter
import com.example.athro.data.model.adapters.ProfileTutorRecyclerAdapter
import com.example.athro.data.model.adapters.ReviewRecyclerAdapter
import com.example.athro.databinding.FragmentProfileBinding
import com.example.athro.ui.MainViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {
    private lateinit var tutorAdapter: ReviewRecyclerAdapter
    private lateinit var tuteeAdapter: ReviewRecyclerAdapter
    private lateinit var profileTutorRecyclerAdapter: ProfileTutorRecyclerAdapter
    private lateinit var profileTuteeRecyclerAdapter: ProfileTuteeRecyclerAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var progUser: User
    private lateinit var database: DatabaseReference
    private var _binding: FragmentProfileBinding? = null

    private var sciences: List<Subject> = listOf(
        Subject.MATHEMATICS,
        Subject.BIOLOGY,
        Subject.CHEMISTRY,
        Subject.PHYSICS,
        Subject.CS
    )
    private var languages: List<Subject> = listOf(
        Subject.ENGLISH,
        Subject.HIGHER_CHINESE,
        Subject.CHINESE,
        Subject.HIGHER_MALAY,
        Subject.MALAY,
        Subject.HIGHER_TAMIL,
        Subject.TAMIL,
        Subject.HINDI,
        Subject.FRENCH,
        Subject.JAPANESE
    )
    private var humanities: List<Subject> = listOf(
        Subject.INTEGRATED_HUMANITIES,
        Subject.ART,
        Subject.MUSIC,
        Subject.GEOGRAPHY,
        Subject.HISTORY,
        Subject.ENGLISH_LITERATURE,
        Subject.ECONOMICS
    )

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tutorAdapter = ReviewRecyclerAdapter()
        tuteeAdapter = ReviewRecyclerAdapter()

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mainViewModel.progUser.value = requireActivity().intent.getParcelableExtra("progUser")

        database = Firebase.database.reference
        progUser = mainViewModel.progUser.value!!
        profileTutorRecyclerAdapter = ProfileTutorRecyclerAdapter(this, database, progUser)
        profileTuteeRecyclerAdapter = ProfileTuteeRecyclerAdapter(this, database, progUser)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.name.text = progUser.name
        binding.year.text = String.format(getString(R.string.year_what), progUser.year)

        if (progUser.tutor != null) {
            val tutorRecyclerView = binding.tutorRecyclerView
            val tutorSciencesChipGroup = binding.tutorSciencesChipGroup
            val tutorLanguagesChipGroup = binding.tutorLanguagesChipGroup
            val tutorHumanitiesChipGroup = binding.tutorHumanitiesChipGroup
            val tutorUpdateButton = binding.tutorUpdateButton
            val tutorCancelButton = binding.tutorCancelButton

            binding.tutorRatingCount.text =
                if (progUser.tutor!!.reviews.size == 1) getString(R.string.one_review)
                else String.format(
                    getString(R.string.multiple_reviews),
                    progUser.tutor!!.reviews.size
                )
            binding.tutorRatingBar.rating = progUser.tutor!!.rating

            tutorRecyclerView.adapter = tutorAdapter
            val layoutManager = LinearLayoutManager(context)
            tutorRecyclerView.layoutManager = layoutManager

            binding.myTuteesRecyclerView.adapter = profileTutorRecyclerAdapter
            val layoutManager2 = LinearLayoutManager(context)
            binding.myTuteesRecyclerView.layoutManager = layoutManager2


            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (dataSnapshot1 in dataSnapshot.children) {
                        for (dataSnapshot2 in dataSnapshot1.children) {
                            val user = User(JSONObject(dataSnapshot2.value as HashMap<*, *>))
                            if (progUser.tutor!!.tutees.contains(user.email))
                                profileTutorRecyclerAdapter.students.add(user)
                        }
                    }
                    profileTutorRecyclerAdapter.notifyDataSetChanged()

                    binding.tutorNoTutees.visibility =
                        if (profileTutorRecyclerAdapter.students.isEmpty()) View.VISIBLE else View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

            addChips(tutorSciencesChipGroup, sciences, true)
            addChips(tutorLanguagesChipGroup, languages, true)
            addChips(tutorHumanitiesChipGroup, humanities, true)

            tutorUpdateButton.visibility = View.GONE
            tutorCancelButton.visibility = View.GONE

            tutorUpdateButton.setOnClickListener {
                val intermediateProgUserSubjects: ArrayList<Subject> = ArrayList()

                tutorSciencesChipGroup.forEach { view ->
                    val chip = view as Chip
                    if (chip.isChecked) {
                        intermediateProgUserSubjects.add(Subject.getSubject(chip.text, this)!!)
                    }
                }

                tutorLanguagesChipGroup.forEach { view ->
                    val chip = view as Chip
                    if (chip.isChecked) {
                        intermediateProgUserSubjects.add(Subject.getSubject(chip.text, this)!!)
                    }
                }

                tutorHumanitiesChipGroup.forEach { view ->
                    val chip = view as Chip
                    if (chip.isChecked) {
                        intermediateProgUserSubjects.add(Subject.getSubject(chip.text, this)!!)
                    }
                }

                if (intermediateProgUserSubjects.size == 0) {
                    tutorSciencesChipGroup.removeAllViews()
                    tutorLanguagesChipGroup.removeAllViews()
                    tutorHumanitiesChipGroup.removeAllViews()
                    addChips(tutorSciencesChipGroup, sciences, true)
                    addChips(tutorLanguagesChipGroup, languages, true)
                    addChips(tutorHumanitiesChipGroup, humanities, true)

                    Toast.makeText(
                        context,
                        getString(R.string.at_least_one_subject),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    progUser.tutor!!.subjects = intermediateProgUserSubjects
                    database.child("users").child(progUser.email!!.substring(0, 8))
                        .setValue(progUser)
                    Toast.makeText(
                        context,
                        getString(R.string.selection_updated),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                tutorUpdateButton.visibility = View.GONE
                tutorCancelButton.visibility = View.GONE
            }

            tutorCancelButton.setOnClickListener {
                tutorSciencesChipGroup.removeAllViews()
                tutorLanguagesChipGroup.removeAllViews()
                tutorHumanitiesChipGroup.removeAllViews()
                addChips(tutorSciencesChipGroup, sciences, true)
                addChips(tutorLanguagesChipGroup, languages, true)
                addChips(tutorHumanitiesChipGroup, humanities, true)
                Toast.makeText(context, getString(R.string.selection_cancelled), Toast.LENGTH_SHORT)
                    .show()
                tutorUpdateButton.visibility = View.GONE
                tutorCancelButton.visibility = View.GONE
            }

            tutorAdapter.reviews = progUser.tutor!!.reviews

            binding.tutorRecyclerView.visibility =
                if (tutorAdapter.reviews.isEmpty()) View.GONE else View.VISIBLE
            binding.tutorNoReviews.visibility =
                if (tutorAdapter.reviews.isEmpty()) View.VISIBLE else View.GONE
        } else {
            binding.tutorCard.visibility = View.GONE
        }

        if (progUser.tutee != null) {
            val tuteeRecyclerView = binding.tuteeRecyclerView
            val tuteeSciencesChipGroup = binding.tuteeSciencesChipGroup
            val tuteeLanguagesChipGroup = binding.tuteeLanguagesChipGroup
            val tuteeHumanitiesChipGroup = binding.tuteeHumanitiesChipGroup
            val tuteeUpdateButton = binding.tuteeUpdateButton
            val tuteeCancelButton = binding.tuteeCancelButton

            binding.tuteeRatingCount.text =
                if (progUser.tutee!!.reviews.size == 1) getString(R.string.one_review)
                else String.format(
                    getString(R.string.multiple_reviews),
                    progUser.tutee!!.reviews.size
                )
            binding.tuteeRatingBar.rating =
                if (progUser.tutee!!.reviews.size == 0) 0F
                else progUser.tutee!!.rating

            tuteeRecyclerView.adapter = tuteeAdapter
            val layoutManager = LinearLayoutManager(context)
            tuteeRecyclerView.layoutManager = layoutManager

            binding.myTutorsRecyclerView.adapter = profileTuteeRecyclerAdapter
            val layoutManager2 = LinearLayoutManager(context)
            binding.myTutorsRecyclerView.layoutManager = layoutManager2

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (dataSnapshot1 in dataSnapshot.children) {
                        for (dataSnapshot2 in dataSnapshot1.children) {
                            val user = User(JSONObject(dataSnapshot2.value as HashMap<*, *>))
                            if (progUser.tutee!!.tutors.contains(user.email))
                                profileTuteeRecyclerAdapter.teachers.add(user)
                        }
                    }
                    profileTuteeRecyclerAdapter.notifyDataSetChanged()

                    binding.tuteeNoTutors.visibility =
                        if (profileTuteeRecyclerAdapter.teachers.isEmpty()) View.VISIBLE else View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

            addChips(tuteeSciencesChipGroup, sciences, false)
            addChips(tuteeLanguagesChipGroup, languages, false)
            addChips(tuteeHumanitiesChipGroup, humanities, false)

            tuteeUpdateButton.visibility = View.GONE
            tuteeCancelButton.visibility = View.GONE

            tuteeUpdateButton.setOnClickListener {
                val intermediateProgUserSubjects: ArrayList<Subject> = ArrayList()

                tuteeSciencesChipGroup.forEach { view ->
                    val chip = view as Chip
                    if (chip.isChecked) {
                        intermediateProgUserSubjects.add(Subject.getSubject(chip.text, this)!!)
                    }
                }

                tuteeLanguagesChipGroup.forEach { view ->
                    val chip = view as Chip
                    if (chip.isChecked) {
                        intermediateProgUserSubjects.add(Subject.getSubject(chip.text, this)!!)
                    }
                }

                tuteeHumanitiesChipGroup.forEach { view ->
                    val chip = view as Chip
                    if (chip.isChecked) {
                        intermediateProgUserSubjects.add(Subject.getSubject(chip.text, this)!!)
                    }
                }

                if (intermediateProgUserSubjects.size == 0) {
                    tuteeSciencesChipGroup.removeAllViews()
                    tuteeLanguagesChipGroup.removeAllViews()
                    tuteeHumanitiesChipGroup.removeAllViews()
                    addChips(tuteeSciencesChipGroup, sciences, false)
                    addChips(tuteeLanguagesChipGroup, languages, false)
                    addChips(tuteeHumanitiesChipGroup, humanities, false)

                    Toast.makeText(
                        context,
                        getString(R.string.at_least_one_subject),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    progUser.tutee!!.subjects = intermediateProgUserSubjects
                    database.child("users").child(progUser.email!!.substring(0, 8))
                        .setValue(progUser)
                    Toast.makeText(
                        context,
                        getString(R.string.selection_updated),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                tuteeUpdateButton.visibility = View.GONE
                tuteeCancelButton.visibility = View.GONE
            }

            tuteeCancelButton.setOnClickListener {
                tuteeSciencesChipGroup.removeAllViews()
                tuteeLanguagesChipGroup.removeAllViews()
                tuteeHumanitiesChipGroup.removeAllViews()
                addChips(tuteeSciencesChipGroup, sciences, false)
                addChips(tuteeLanguagesChipGroup, languages, false)
                addChips(tuteeHumanitiesChipGroup, humanities, false)
                Toast.makeText(context, getString(R.string.selection_cancelled), Toast.LENGTH_SHORT)
                    .show()
                tuteeUpdateButton.visibility = View.GONE
                tuteeCancelButton.visibility = View.GONE
            }

            tuteeAdapter.reviews = progUser.tutee!!.reviews

            binding.tuteeRecyclerView.visibility =
                if (tuteeAdapter.reviews.isEmpty()) View.GONE else View.VISIBLE
            binding.tuteeNoReviews.visibility =
                if (tuteeAdapter.reviews.isEmpty()) View.VISIBLE else View.GONE
        } else {
            binding.tuteeCard.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addChips(chipGroup: ChipGroup, list: List<Subject>, isTutor: Boolean) {
        list.forEach { subject ->
            val chip = Chip(context)
            chip.text = subject.getName(this)
            chip.isCheckable = true
            chip.setChipBackgroundColorResource(R.color.chip_background_color)

            if (isTutor) {
                mainViewModel.progUser.value!!.tutor!!.subjects.forEach { s ->
                    if (chip.text.toString() == s.getName(this)) {
                        chip.isChecked = true
                    }
                }

                chip.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                    binding.tutorUpdateButton.visibility = View.VISIBLE
                    binding.tutorCancelButton.visibility = View.VISIBLE
                }
            } else {
                mainViewModel.progUser.value!!.tutee!!.subjects.forEach { s ->
                    if (chip.text.toString() == s.getName(this)) {
                        chip.isChecked = true
                    }
                }

                chip.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                    binding.tuteeUpdateButton.visibility = View.VISIBLE
                    binding.tuteeCancelButton.visibility = View.VISIBLE
                }
            }

            chipGroup.addView(chip)
        }
    }
}