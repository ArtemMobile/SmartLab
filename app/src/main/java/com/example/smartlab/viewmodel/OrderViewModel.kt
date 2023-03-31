package com.example.smartlab.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartlab.model.api.OsmClient
import com.example.smartlab.model.api.SmartLabClient
import com.example.smartlab.model.dto.OrderId
import com.example.smartlab.model.dto.OrderRequest
import com.example.smartlab.model.dto.ReverseGeocoding
import com.example.smartlab.utils.DataStore
import kotlinx.coroutines.launch

class OrderViewModel(private val app: Application) : AndroidViewModel(app) {

    var currentLocation = MutableLiveData<Location>()
    private val _reversedGeocoding = MutableLiveData<ReverseGeocoding>()
    val reversedGeocoding = _reversedGeocoding

    private val _orderResponse = MutableLiveData<OrderId> ()
    val orderResponse = _orderResponse

    fun order(orderRequest: OrderRequest) {
        viewModelScope.launch {
            val response =
                SmartLabClient.retrofit.order("Bearer ${DataStore.decryptToken()}", orderRequest)
            if (response.isSuccessful) {
                response.body()?.let {
                    _orderResponse.value = it
                }
            }
        }
    }

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