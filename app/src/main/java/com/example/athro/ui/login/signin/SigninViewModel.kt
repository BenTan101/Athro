package com.example.athro.ui.login.signin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.athro.data.model.User

class SigninViewModel : ViewModel() {
    var hasAnimationPlayed: MutableLiveData<Boolean> = MutableLiveData(false)
}