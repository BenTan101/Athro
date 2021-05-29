package com.example.athro.ui.login.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.athro.R
import com.example.athro.data.model.User
import com.example.athro.ui.MainActivity
import com.example.athro.ui.MainViewModel
import com.example.athro.ui.login.signin.SigninViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.util.*


class SignupFragment : Fragment() {
    private lateinit var name: EditText
    private lateinit var yearSpinner: Spinner
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var tutor: CheckBox
    private lateinit var tutee: CheckBox
    private lateinit var signUp: Button

    private lateinit var signinViewModel: SigninViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var mAuth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private var progUser: User = User()
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        signinViewModel = ViewModelProvider(requireActivity()).get(SigninViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        mAuth = Firebase.auth
        database = Firebase.database.reference
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = view.findViewById(R.id.name)
        yearSpinner = view.findViewById(R.id.year_spinner)
        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        tutor = view.findViewById(R.id.user)
        tutee = view.findViewById(R.id.tutee)
        signUp = view.findViewById(R.id.sign_up)

        ArrayAdapter.createFromResource(
            view.context,
            R.array.year_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            yearSpinner.adapter = adapter
        }

        yearSpinner.setSelection(0)

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                (parent.getChildAt(0) as TextView).setTextColor(
                    resources.getColor(
                        R.color.white,
                        activity!!.theme
                    )
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        signUp.setOnClickListener { it ->
            if (!validate()) return@setOnClickListener

            val v = it
            activity?.let { it1 ->
                Log.d("Test", "signIn: " + email.text.toString().toLowerCase())
                mAuth.createUserWithEmailAndPassword(
                    email.text.toString().toLowerCase(),
                    password.text.toString()
                ).addOnCompleteListener(it1) { task ->
                    if (task.isSuccessful) {
                        user = mAuth.currentUser!!
                        progUser = User(
                            name.text.toString(),
                            email.text.toString().toLowerCase(),
                            yearSpinner.selectedItem.toString(),
                            tutor.isChecked,
                            tutee.isChecked,
                        )

                        database.child("users")
                            .child(email.text.toString().substring(0, 8))
                            .setValue(progUser)
                        Toast.makeText(
                            context, resources.getString(R.string.successful_signup),
                            Toast.LENGTH_SHORT
                        ).show()
                        mainViewModel.progUser.value = progUser
                        finishSignIn()
                    } else {
                        mAuth.signInWithEmailAndPassword(
                            email.text.toString().toLowerCase(),
                            password.text.toString()
                        ).addOnCompleteListener(it1) { task ->
                            if (task.isSuccessful) {
                                Log.d("Test", "sigInWithEmail:success")
                                user = mAuth.currentUser!!
                                database.child("users").child(
                                    email.text.toString().toLowerCase().replace(".", "")
                                ).get().addOnSuccessListener {
                                    progUser =
                                        User(JSONObject(it.value as HashMap<*, *>))
                                    Toast.makeText(
                                        context,
                                        resources.getString(R.string.existing_account),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finishSignIn()
                                }.addOnFailureListener {
                                    Log.d("Test", "Failed")
                                    Toast.makeText(
                                        context, resources.getString(R.string.auth_failed),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Navigation.findNavController(v)
                                        .navigate(R.id.action_signupFragment_to_signinFragment)
                                }
                            } else {
                                Toast.makeText(
                                    context, resources.getString(R.string.auth_failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                                Navigation.findNavController(v)
                                    .navigate(R.id.action_signupFragment_to_signinFragment)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        // Check that there are no empty fields.
        if (name.text.isEmpty()) {
            name.error = resources.getString(R.string.required_field)
            isValid = false
        }

        if (name.text.length > 40) {
            name.error = getString(R.string.long_name)
            isValid = false
        }

        val address = email.text.toString().toLowerCase()
        val year = Calendar.getInstance().get(Calendar.YEAR).toString().substring(2).toInt()

        if (!address.matches(Regex("h[0-9]{3}0[0-9]{3}@nushigh\\.edu\\.sg"))
            || address.substring(1, 3).toInt() > year
            || (address[3].toString().toInt() != 1 && address[3].toString().toInt() != 3)
            || address.substring(5, 8).toInt() == 0
            || address[5].toString().toInt() > 1
        ) {
            email.error = resources.getString(R.string.invalid_email)
            isValid = false
        }

        if (password.text.length < 8) {
            password.error = resources.getString(R.string.short_password)
            isValid = false
        }

        if (!tutor.isChecked && !tutee.isChecked) {
            tutee.error = resources.getString(R.string.boxes_unchecked)
            isValid = false
        }

        return isValid
    }

    private fun finishSignIn() {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("user", user)
        intent.putExtra("progUser", progUser)
        startActivity(intent)
    }
}