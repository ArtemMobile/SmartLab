package com.example.smartlab.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class OrderViewModel(private val app: Application) : AndroidViewModel(app) {

    var currentLocation = MutableLiveData<Location>()
}