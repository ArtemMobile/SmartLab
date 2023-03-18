package com.example.smartlab.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartlab.model.api.SmartLabClient
import com.example.smartlab.model.api.callModels.ProfileCall
import com.example.smartlab.model.api.responseModels.ProfileResponse
import com.example.smartlab.utils.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(private val app: Application) : AndroidViewModel(app) {

    private var _profile = MutableLiveData<ProfileResponse>()
    val profile = _profile
    private var _token = MutableLiveData<String>()


    fun createProfile(profile: ProfileCall){
        getToken()
        Log.d("TOKEN:",  _token.value.toString())
        viewModelScope.launch(Dispatchers.IO) {
            val response = SmartLabClient.retrofit.createProfile("application/json", "Bearer ${_token.value.toString()}", profile)
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    _profile.value = response.body()
                    Log.d("PROFILE:",  _profile.value!!.updated_at)
                }
            }

        }
    }

    private fun getToken(){
        viewModelScope.launch {
            DataStore.getToken(app).collect{
                _token.value = it
            }
        }
    }
}