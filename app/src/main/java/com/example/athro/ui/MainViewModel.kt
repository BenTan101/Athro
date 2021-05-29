package com.example.athro.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.athro.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainViewModel : ViewModel() {
    var progUser: MutableLiveData<User> = MutableLiveData()
}