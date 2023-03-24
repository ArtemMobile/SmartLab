package com.example.smartlab.viewmodel

import android.app.Application
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartlab.model.api.SmartLabClient
import com.example.smartlab.model.api.callModels.ProfileRequest
import com.example.smartlab.model.api.responseModels.ProfileResponse
import com.example.smartlab.utils.DataStore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(private val app: Application) : AndroidViewModel(app) {

    private var token: String = ""
    private val TAG = this::class.java.simpleName

    private val _patientCard = MutableLiveData<ProfileResponse?>()
    val patientCard = _patientCard

    private val _createProfileStatus = MutableLiveData<ProfileResponse>()
    val createProfileStatus = _createProfileStatus

    var isEditMode = false

    init {
        viewModelScope.launch {
            DataStore.getToken(app).collect {
                token = it
            }
        }
    }

    fun getPatientCard() {
        viewModelScope.launch {
            DataStore.getPatientCard(app).collect {
                _patientCard.value = it
            }
        }
    }

    fun createProfile(profile: ProfileRequest) {
        viewModelScope.launch {
            val response = SmartLabClient.retrofit.createProfile("Bearer $token", profile)
            if (response.isSuccessful) {
                _createProfileStatus.value = response.body()
                DataStore.savePatientCard(app, response.body()!!).collect{
                    Log.d("USER SAVED", "$it")
                }
            }
        }
    }

    fun updateProfile(profile: ProfileRequest) {
        viewModelScope.launch {
            val response = SmartLabClient.retrofit.updateProfile("Bearer $token", profile)
            if (response.isSuccessful) {
                Log.d(TAG, "updateProfile: success")
                DataStore.savePatientCard(app, response.body()!!).collect{
                    Log.d("USER UPDATED ", "$it")
                }

            } else {
                Log.d(TAG, "updateProfile: error ${response.message()}")
            }
        }
    }
}