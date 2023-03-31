package com.example.smartlab.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartlab.model.api.OsmClient
import com.example.smartlab.model.dto.ReverseGeocoding
import kotlinx.coroutines.launch

class OrderViewModel(private val app: Application) : AndroidViewModel(app) {

    var currentLocation = MutableLiveData<Location>()
    private val _reversedGeocoding = MutableLiveData<ReverseGeocoding>()
    val reversedGeocoding = _reversedGeocoding

    fun reverseGeocoding(lat: Double, lon: Double) {
        viewModelScope.launch {
            val response = OsmClient.retrofit.reverseGeocode(lat, lon)
            if (response.isSuccessful) {
                response.body()?.let {
                    _reversedGeocoding.value = it
                }
            }
        }
    }
}