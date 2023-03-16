package com.example.smartlab.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartlab.model.api.SmartLabClient
import com.example.smartlab.model.api.responseModels.ErrorResponse
import com.example.smartlab.utils.DataStore
import com.example.smartlab.utils.SaveStatus
import com.google.gson.Gson
import kotlinx.coroutines.launch

class EmailCodeViewModel(private val app: Application) : AndroidViewModel(app)  {
    private val TAG = this::class.simpleName

    private val _signInStatus: MutableLiveData<SaveStatus> = MutableLiveData()
    val signInStatus: LiveData<SaveStatus> = _signInStatus

    private val _email: MutableLiveData<String> = MutableLiveData()
    val email: LiveData<String> = _email

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error

    fun signIn(email: String, code: String) {
        viewModelScope.launch {
            val response = SmartLabClient.retrofit.signIn(email, code)
            if (response.isSuccessful) {
                response.body()?.let { tokenResponse ->
                    Log.d(TAG, "signIn: token: ${tokenResponse.token}")
                    DataStore.saveToken(app, tokenResponse.token).collect { saveStatus ->
                        _signInStatus.value = saveStatus
                    }
                }
            } else {
                val error = Gson().fromJson(response.errorBody()?.string()?: "{}", ErrorResponse::class.java)
                Log.d(TAG, error.errors)
                _error.value = error.errors
            }
        }
    }

    fun getEmail() {
        viewModelScope.launch {
            DataStore.getEmail(app).collect {
                _email.value = it
            }
        }
    }

    fun clearEmail() {
        _email.value = ""
    }
}