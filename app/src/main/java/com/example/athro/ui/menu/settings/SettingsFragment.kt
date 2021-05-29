package com.example.athro.ui.menu.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.athro.R
import com.example.athro.data.model.Tutee
import com.example.athro.data.model.Tutor
import com.example.athro.data.model.User
import com.example.athro.databinding.FragmentSettingsBinding
import com.example.athro.ui.MainViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private lateinit var progUser: User
    private lateinit var database: DatabaseReference
    private lateinit var mainViewModel: MainViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        mainViewModel.progUser.value = requireActivity().intent.getParcelableExtra("progUser")
        progUser = mainViewModel.progUser.value!!
        database = Firebase.database.reference

        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tutorWarning.visibility = View.GONE
        binding.tuteeWarning.visibility = View.GONE

        binding.tutorSwitch.isChecked = progUser.tutor != null
        binding.tutorSwitch.text =
            if (progUser.tutor != null) getString(R.string.you_re_a_tutor) else getString(R.string.you_re_not_a_tutor)
        binding.tuteeSwitch.isChecked = progUser.tutee != null
        binding.tuteeSwitch.text =
            if (progUser.tutee != null) getString(R.string.you_re_a_tutee) else getString(R.string.you_re_not_a_tutee)

        binding.buttonsLayout.visibility = View.GONE

        binding.tutorSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            binding.tutorSwitch.text =
                if (b) getString(R.string.you_re_a_tutor) else getString(R.string.you_re_not_a_tutor)
            if (!b && progUser.tutor != null && progUser.tutor!!.tutees.isNotEmpty())
                binding.tutorWarning.visibility = View.VISIBLE
            else
                binding.tutorWarning.visibility = View.GONE

            binding.buttonsLayout.visibility = View.VISIBLE
        }

        binding.tuteeSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            binding.tuteeSwitch.text =
                if (b) getString(R.string.you_re_a_tutee) else getString(R.string.you_re_not_a_tutee)
            if (!b && progUser.tutee != null && progUser.tutee!!.tutors.isNotEmpty())
                binding.tuteeWarning.visibility = View.VISIBLE
            else
                binding.tuteeWarning.visibility = View.GONE

            binding.buttonsLayout.visibility = View.VISIBLE
        }

        binding.updateButton.setOnClickListener {
            // Tutor
            if (progUser.tutor == null && binding.tutorSwitch.isChecked) {
                progUser.tutor = Tutor()
                database.child("users").child(progUser.email!!.substring(0, 8)).setValue(progUser)
            } else if (progUser.tutor != null && !binding.tutorSwitch.isChecked) {
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

                                    if (user.tutee!!.tutors.contains(progUser.email)) {
                                        user.tutee!!.tutors.remove(progUser.email)
                                        database.child("users").child(user.email!!.substring(0, 8))
                                            .setValue(user)
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })

                progUser.tutor = null
                database.child("users").child(progUser.email!!.substring(0, 8)).setValue(progUser)
            }

            // Tutee
            if (progUser.tutee == null && binding.tuteeSwitch.isChecked) {
                progUser.tutee = Tutee()
                database.child("users").child(progUser.email!!.substring(0, 8)).setValue(progUser)
            } else if (progUser.tutee != null && !binding.tuteeSwitch.isChecked) {
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (dataSnapshot1 in dataSnapshot.children) {
                            for (dataSnapshot2 in dataSnapshot1.children) {
                                val user = User(JSONObject(dataSnapshot2.value as HashMap<*, *>))
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
                                try {
                                    user.tutor =
                                        Tutor(
                                            JSONObject(dataSnapshot2.value as HashMap<*, *>).getJSONObject(
                                                "tutor"
                                            )
                                        )

                                    if (user.tutor!!.tutees.contains(progUser.email)) {
                                        user.tutor!!.tutees.remove(progUser.email)
                                        database.child("users").child(user.email!!.substring(0, 8))
                                            .setValue(user)
                                    }

                                    if (user.tutor!!.requests.contains(progUser.email)) {
                                        user.tutor!!.requests.remove(progUser.email)
                                        database.child("users").child(user.email!!.substring(0, 8))
                                            .setValue(user)
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })

                progUser.tutee = null
                database.child("users").child(progUser.email!!.substring(0, 8)).setValue(progUser)
            }

            Toast.makeText(context, getString(R.string.changes_saved), Toast.LENGTH_SHORT).show()

            binding.buttonsLayout.visibility = View.GONE
            binding.tutorWarning.visibility = View.GONE
            binding.tuteeWarning.visibility = View.GONE
        }

        binding.cancelButton.setOnClickListener {
            binding.tutorSwitch.isChecked = progUser.tutor != null
            binding.tutorSwitch.text =
                if (progUser.tutor != null) getString(R.string.you_re_a_tutor) else getString(R.string.you_re_not_a_tutor)
            binding.tuteeSwitch.isChecked = progUser.tutee != null
            binding.tuteeSwitch.text =
                if (progUser.tutor != null) getString(R.string.you_re_a_tutee) else getString(R.string.you_re_not_a_tutee)

            binding.tutorWarning.visibility = View.GONE
            binding.tuteeWarning.visibility = View.GONE
            binding.buttonsLayout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}