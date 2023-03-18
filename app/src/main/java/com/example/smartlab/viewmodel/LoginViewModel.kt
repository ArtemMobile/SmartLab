package com.example.smartlab.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartlab.utils.SendCodeStatus
import com.example.smartlab.utils.SaveStatus
import com.example.smartlab.model.api.SmartLabClient
import com.example.smartlab.utils.DataStore
import kotlinx.coroutines.launch

class LoginViewModel(private val app: Application): AndroidViewModel(app) {

    private val _sendCodeStatus: MutableLiveData<SendCodeStatus> = MutableLiveData()
    val sendCodeStatus: LiveData<SendCodeStatus> = _sendCodeStatus

    private val _saveEmailStatus: MutableLiveData<SaveStatus> = MutableLiveData()
    val saveEmailStatus: LiveData<SaveStatus> = _saveEmailStatus

    fun sendCode(email: String) {
        viewModelScope.launch {
            when(SmartLabClient.retrofit.sendCode(email).code()) {
                200 -> _sendCodeStatus.value = SendCodeStatus.SUCCESS
                422 -> _sendCodeStatus.value = SendCodeStatus.FAIL
            }
        }
    }

    fun clearSendCodeStatus() {
        _sendCodeStatus.value = SendCodeStatus.NOTHING
    }

    fun saveEmail(email: String) {
        viewModelScope.launch {
            DataStore.saveEmail(app, email).collect {
                _saveEmailStatus.value = it
            }
        }
    }
}

