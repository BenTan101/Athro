package com.example.athro.ui.main.tutees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.athro.data.model.adapters.TuteeRequestRecyclerAdapter
import com.example.athro.data.model.User
import com.example.athro.databinding.FragmentTuteesBinding
import com.example.athro.ui.MainViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.util.*

class TuteesFragment : Fragment() {
    private lateinit var requestAdapter: TuteeRequestRecyclerAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var tuteesViewModel: TuteesViewModel
    private lateinit var progUser: User
    private lateinit var database: DatabaseReference
    private var _binding: FragmentTuteesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        tuteesViewModel = ViewModelProvider(requireActivity()).get(TuteesViewModel::class.java)

        progUser = mainViewModel.progUser.value!!
        database = Firebase.database.reference

        _binding = FragmentTuteesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // I.e. The user is not a tutor and cannot log hours.
        if (progUser.tutor == null) {
            binding.isTutorLayout.visibility = View.GONE
            binding.notTutorLayout.visibility = View.VISIBLE
            return
        } else {
            binding.notTutorLayout.visibility = View.GONE
        }

        requestAdapter = TuteeRequestRecyclerAdapter(this, database, progUser, binding.noRequests)

        val tutorsRecyclerView = binding.requestRecyclerView

        tutorsRecyclerView.adapter = requestAdapter
        val layoutManager = LinearLayoutManager(context)
        tutorsRecyclerView.layoutManager = layoutManager

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                requestAdapter.requests.clear()

                for (dataSnapshot1 in dataSnapshot.children) {
                    for (dataSnapshot2 in dataSnapshot1.children) {
                        val user = User(JSONObject(dataSnapshot2.value as HashMap<*, *>))
                        if (progUser.tutor!!.requests.contains(user.email))
                            requestAdapter.requests.add(user)
                    }
                }
                requestAdapter.notifyDataSetChanged()

                binding.noRequests.visibility =
                    if (requestAdapter.requests.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        tuteesViewModel.requests.observe(viewLifecycleOwner, {
            binding.noRequests.visibility =
                if (requestAdapter.requests.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}