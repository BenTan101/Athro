package com.example.athro.ui.login.signin

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.athro.R
import com.example.athro.data.model.User
import com.example.athro.databinding.FragmentSigninBinding
import com.example.athro.ui.MainActivity
import com.example.athro.ui.MainViewModel
import com.example.athro.ui.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.util.*


class SigninFragment : Fragment() {
    private lateinit var signinViewModel: SigninViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var _binding: FragmentSigninBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signinViewModel = ViewModelProvider(requireActivity()).get(SigninViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        mAuth = Firebase.auth
        database = Firebase.database.reference

        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUp.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_signinFragment_to_signupFragment)
        }

        binding.signIn.setOnClickListener {
            if (binding.email.text.isEmpty() || binding.password.text.isEmpty()) {
                Toast.makeText(
                    context, resources.getString(R.string.empty_fields),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            activity?.let { it1 ->
                mAuth.signInWithEmailAndPassword(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                ).addOnCompleteListener(
                    it1
                ) { task ->
                    if (task.isSuccessful) {
                        Log.d("Test", "signInWithEmail:success")
                        database.child("users")
                            .child(binding.email.text.substring(0, 8))
                            .get().addOnSuccessListener {
                                mainViewModel.progUser.value =
                                    User(JSONObject(it.value as HashMap<*, *>))
                                Toast.makeText(
                                    context, resources.getString(R.string.successful_signin),
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(context, MainActivity::class.java)
                                intent.putExtra("progUser", mainViewModel.progUser.value)
                                startActivity(intent)
                            }.addOnFailureListener {
                                Toast.makeText(
                                    context, resources.getString(R.string.auth_failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    } else {
                        Toast.makeText(
                            context, resources.getString(R.string.auth_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.fab.setOnClickListener {
            val intent = Intent(context, OnboardingActivity::class.java)
            startActivity(intent)
        }

//        try {
        if (!signinViewModel.hasAnimationPlayed.value!!) {
            // Weird error occurs if screen rotation occurs during animation, so screen orientation needs to be locked.
            if (requireActivity().windowManager.defaultDisplay.rotation == Surface.ROTATION_0)
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            if (requireActivity().windowManager.defaultDisplay.rotation == Surface.ROTATION_90)
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            if (requireActivity().windowManager.defaultDisplay.rotation == Surface.ROTATION_270)
                requireActivity().requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE

            val helloColorFrom = resources.getColor(R.color.black)
            val helloColorTo = resources.getColor(R.color.light_orange)
            val helloColorAnimation =
                ValueAnimator.ofObject(ArgbEvaluator(), helloColorFrom, helloColorTo)
            helloColorAnimation.duration = 2500

            val hello: TextView = view.findViewById(R.id.hello)
            val welcome: TextView = view.findViewById(R.id.welcome)
            welcome.setTextColor(resources.getColor(R.color.black))

            helloColorAnimation.addUpdateListener { animator ->
                hello.setTextColor(animator.animatedValue as Int)
            }
            helloColorAnimation.start()

            helloColorAnimation.doOnEnd {
                val welcomeColorFrom =
                    resources.getColor(R.color.black)
                val welcomeColorTo = resources.getColor(R.color.white)
                val welcomeColorAnimation =
                    ValueAnimator.ofObject(ArgbEvaluator(), welcomeColorFrom, welcomeColorTo)
                welcomeColorAnimation.duration = 2000

                welcomeColorAnimation.addUpdateListener { animator ->
                    welcome.setTextColor(animator.animatedValue as Int)
                }
                welcomeColorAnimation.start()

                signinViewModel.hasAnimationPlayed.value = true

                requireActivity().requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}