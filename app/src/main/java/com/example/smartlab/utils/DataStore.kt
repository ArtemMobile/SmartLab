package com.example.smartlab.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.smartlab.model.api.responseModels.ProfileResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

object DataStore {
    val IS_ONBOARDING_PASSED = booleanPreferencesKey("isOnboardingPassed")
    val TOKEN = stringPreferencesKey("token")
    val EMAIL = stringPreferencesKey("email")
    val PROFILE = stringPreferencesKey("profile")
    val IS_CREATE_PATIENT_CARD_PASSED = booleanPreferencesKey("isCreatePatientCardPassed")

    suspend fun saveToken(context: Context, token: String): Flow<SaveStatus> {
        return flow {
            context.dataStore.edit {
                it[TOKEN] = token
                emit(SaveStatus.SUCCESS)
            }
        }
    }

    fun getToken(context: Context): Flow<String> {
        val token = context.dataStore.data.map {
            it[TOKEN] ?: ""
        }
        return token
    }

    suspend fun saveEmail(context: Context, email: String): Flow<SaveStatus> {
        return flow {
            context.dataStore.edit {
                it[EMAIL] = email
                emit(SaveStatus.SUCCESS)
            }
        }
    }

    suspend fun savePatientCard(context: Context, user: ProfileResponse): Flow<SaveStatus>{
        return flow{
            context.dataStore.edit {
                it[PROFILE] = Gson().toJson(user)
                emit(SaveStatus.SUCCESS)
            }
        }
    }

    fun getEmail(context: Context): Flow<String> {
        val email = context.dataStore.data.map {
            it[EMAIL] ?: ""
        }
        return email
    }

    fun getPatientCard(context: Context): Flow<ProfileResponse>{
        val user = context.dataStore.data.map {
            Gson().fromJson(it[PROFILE] ?: "{}",  ProfileResponse::class.java)
        }
        return user
    }

    suspend fun saveCreatePatientCardPassed(context: Context): Flow<SaveStatus> {
        return flow {
            context.dataStore.edit {
                it[IS_CREATE_PATIENT_CARD_PASSED] = true
                emit(SaveStatus.SUCCESS)
            }
        }
    }
}


