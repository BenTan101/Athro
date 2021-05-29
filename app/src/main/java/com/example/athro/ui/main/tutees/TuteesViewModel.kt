package com.example.athro.ui.main.tutees

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.athro.data.model.User

class TuteesViewModel : ViewModel() {
    var requests: MutableLiveData<ArrayList<User>> = MutableLiveData(ArrayList())
}