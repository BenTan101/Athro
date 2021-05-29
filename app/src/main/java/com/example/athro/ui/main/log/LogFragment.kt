package com.example.athro.ui.main.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athro.data.model.adapters.LogTuteeRecyclerAdapter
import com.example.athro.data.model.User
import com.example.athro.databinding.FragmentLogBinding
import com.example.athro.ui.MainViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.util.*

class LogFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var tuteeRecyclerAdapter: LogTuteeRecyclerAdapter
    private lateinit var progUser: User
    private lateinit var database: DatabaseReference
    private var _binding: FragmentLogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        progUser = mainViewModel.progUser.value!!
        database = Firebase.database.reference

        tuteeRecyclerAdapter = LogTuteeRecyclerAdapter(this, database, progUser)

        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // I.e. The user is not a tutor.
        if (progUser.tutor == null) {
            binding.isTutorLayout.visibility = View.GONE
            binding.notTutorLayout.visibility = View.VISIBLE
            return
        } else {
            binding.notTutorLayout.visibility = View.GONE
        }

        binding.tuteesRecyclerView.adapter = tuteeRecyclerAdapter
        val layoutManager = LinearLayoutManager(context)
        binding.tuteesRecyclerView.layoutManager = layoutManager

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    for (dataSnapshot2 in dataSnapshot1.children) {
                        val user = User(JSONObject(dataSnapshot2.value as HashMap<*, *>))
                        if (progUser.tutor!!.tutees.contains(user.email))
                            tuteeRecyclerAdapter.tutees.add(user)
                    }
                }
                tuteeRecyclerAdapter.notifyDataSetChanged()

                binding.noTutees.visibility =
                    if (tuteeRecyclerAdapter.tutees.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}