package com.example.smartlab.app

import android.app.Application
import com.example.smartlab.utils.DataStore
import com.example.smartlab.utils.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class App: Application() {
    lateinit var isOnboardingPassedFlow: Flow<Boolean>
    lateinit var isCreateProfilePassed: Flow<Boolean>

    override fun onCreate() {
        super.onCreate()
        isOnboardingPassedFlow = this.applicationContext.dataStore.data.map {
            it[DataStore.IS_ONBOARDING_PASSED] ?: false
        }
        isCreateProfilePassed = this.applicationContext.dataStore.data.map {
            it[DataStore.IS_CREATE_PATIENT_CARD_PASSED] ?: false
        }
    }
}

