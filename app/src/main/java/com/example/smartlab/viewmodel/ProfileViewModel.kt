package com.example.smartlab.viewmodel

import android.app.Application
import android.net.Uri
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartlab.model.api.SmartLabClient
import com.example.smartlab.model.api.callModels.ProfileRequest
import com.example.smartlab.model.api.responseModels.PhotoErrorResponse
import com.example.smartlab.model.api.responseModels.ProfileResponse
import com.example.smartlab.utils.DataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(private val app: Application) : AndroidViewModel(app) {

    private var token: String = ""
    private val TAG = this::class.java.simpleName

    private val _patientCard = MutableLiveData<ProfileRequest?>()
    val patientCard = _patientCard

    private val _createdProfile = MutableLiveData<ProfileResponse>()
    val createdProfile = _createdProfile

    private val _updatedProfile = MutableLiveData<ProfileResponse>()
    val updatedProfile = _updatedProfile

    private val _imageFileName = MutableLiveData<Uri>()
    val imageFileName = _imageFileName

    private val _avatarError = MutableLiveData<String>()
    val avatarError = _avatarError

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

    fun getImageName(){
        viewModelScope.launch {
            DataStore.getImageUri(app).collect {
                _imageFileName.value = it.toUri()
            }
        }
    }

    fun saveImageToPrefs(image: Uri){
        viewModelScope.launch {
            DataStore.saveImageUri(app, image.toString()).collect {
                Log.d("IMAGE SAVED", "$it")
            }
        }
    }

    fun createProfile(profile: ProfileRequest) {
        viewModelScope.launch {
            val response = SmartLabClient.retrofit.createProfile("Bearer $token", profile)
            if (response.isSuccessful) {
                _createdProfile.value = response.body()
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
                _updatedProfile.value = response.body()!!
                DataStore.savePatientCard(app, response.body()!!).collect{
                    Log.d("USER UPDATED ", "$it")
                }

            } else {
                Log.d(TAG, "updateProfile: error ${response.message()}")
            }
        }
    }

    fun updateAvatar(avatar: MultipartBody.Part) {
        viewModelScope.launch {
            val response = SmartLabClient.retrofit.updateAvatar("Bearer $token", avatar)
            Log.d(TAG, "updateAvatar: ${response.code()}")
            if(!response.isSuccessful){
                val errorResponse = Gson().fromJson(response.errorBody()?.string(), PhotoErrorResponse::class.java)
                _avatarError.value = errorResponse.error.file[0]

            } else{
                _patientCard.value?.let {
                    updateProfile(it)
                }
            }
        }
    }
}