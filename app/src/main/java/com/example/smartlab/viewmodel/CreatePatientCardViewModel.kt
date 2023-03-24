package com.example.smartlab.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartlab.model.api.SmartLabClient
import com.example.smartlab.model.api.callModels.ProfileRequest
import com.example.smartlab.model.api.responseModels.ProfileResponse
import com.example.smartlab.utils.DataStore
import kotlinx.coroutines.launch

class CreatePatientCardViewModel(private val app: Application) : AndroidViewModel(app) {

    private val TAG = this::class.java.simpleName
    private var _profile = MutableLiveData<ProfileResponse>()
    val profile = _profile
    private var _token = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            DataStore.getToken(app).collect {
                _token.value = it
            }
        }
    }

    fun createProfile(profile: ProfileRequest) {
        viewModelScope.launch {
            val response = SmartLabClient.retrofit.createProfile( "Bearer ${_token.value.toString()}", profile)
            if (response.isSuccessful) {
                _profile.value = response.body()
                DataStore.savePatientCard(app, _profile.value!!).collect{
                    Log.d("USER SAVED", "$it")
                }
            }
        }
    }

    fun setCreatePatientCardPassed() {
        viewModelScope.launch {
            DataStore.saveCreatePatientCardPassed(app).collect {
                Log.d(TAG, "setCreatePatientCardPassed: $it")
            }
        }
    }
}