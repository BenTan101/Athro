package com.example.athro.ui.main.tutors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athro.R
import com.example.athro.data.model.Subject
import com.example.athro.data.model.Tutee
import com.example.athro.data.model.Tutor
import com.example.athro.data.model.User
import com.example.athro.data.model.adapters.TutorRecyclerAdapter
import com.example.athro.databinding.FragmentTutorsBinding
import com.example.athro.ui.MainViewModel
import com.google.android.material.chip.Chip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class TutorsFragment : Fragment() {
    private lateinit var tutorAdapter: TutorRecyclerAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var tutors: ArrayList<User>
    private lateinit var progUser: User
    private lateinit var database: DatabaseReference
    private var _binding: FragmentTutorsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        database = Firebase.database.reference

        progUser = mainViewModel.progUser.value!!
        tutorAdapter = TutorRecyclerAdapter(this, database, progUser)

        _binding = FragmentTutorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // I.e. The user is not a tutee and doesn't need to find tutors.
        if (progUser.tutee == null) {
            binding.isTuteeLayout.visibility = View.GONE
            binding.notTuteeLayout.visibility = View.VISIBLE
            return
        } else {
            binding.notTuteeLayout.visibility = View.GONE
        }

        tutors = ArrayList()

        // The subjects the user needs to be taught.
        progUser.tutee!!.subjects.forEach { subject ->
            val chip = Chip(context)
            chip.text = subject.getName(this)
            chip.isCheckable = true
            chip.setChipBackgroundColorResource(R.color.chip_background_color)

            chip.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                tutorAdapter.tutors.clear()
                binding.chipGroup.children.forEach { view ->
                    val thisChip = view as Chip
                    if (thisChip.isChecked) {
                        tutors.forEach { tutor ->
                            if (tutor.tutor!!.subjects.contains(
                                    Subject.getSubject(
                                        thisChip.text.toString(),
                                        this
                                    )
                                )
                                && !(tutorAdapter.tutors.contains(tutor))
                            )
                                tutorAdapter.tutors.add(tutor)
                        }
                    }
                }
                binding.noTutors.visibility =
                    if (tutorAdapter.tutors.isEmpty()) View.VISIBLE else View.GONE

                tutorAdapter.notifyDataSetChanged()
            }

            binding.chipGroup.addView(chip)
        }

        if (binding.chipGroup.childCount == 0) {
            binding.noTutors.text = getString(R.string.no_tutee_subjects)
            binding.noTutors.visibility = View.VISIBLE
        }

        val tutorsRecyclerView = binding.tutorsRecyclerView

        tutorsRecyclerView.adapter = tutorAdapter
        val layoutManager = LinearLayoutManager(context)
        tutorsRecyclerView.layoutManager = layoutManager

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    for (dataSnapshot2 in dataSnapshot1.children) {
                        val user = User(JSONObject(dataSnapshot2.value as HashMap<*, *>))
                        try {
                            user.tutor =
                                Tutor(
                                    JSONObject(dataSnapshot2.value as HashMap<*, *>).getJSONObject(
                                        "tutor"
                                    )
                                )
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        try {
                            user.tutee =
                                Tutee(
                                    JSONObject(dataSnapshot2.value as HashMap<*, *>).getJSONObject(
                                        "tutee"
                                    )
                                )
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        if (user != progUser && (user.tutor != null))
                            tutors.add(user)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}