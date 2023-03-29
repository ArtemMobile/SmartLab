package com.example.smartlab.utils

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.smartlab.model.api.callModels.ProfileRequest
import com.example.smartlab.model.api.responseModels.ProfileResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

object DataStore {

    private lateinit var masterKey: MasterKey
    private lateinit var sharedPreferences: EncryptedSharedPreferences

    val IS_ONBOARDING_PASSED = booleanPreferencesKey("isOnboardingPassed")
    val EMAIL = stringPreferencesKey("email")
    val PROFILE = stringPreferencesKey("profile")
    val PROFILE_IMAGE = stringPreferencesKey("profile_image")
    val IS_CREATE_PATIENT_CARD_PASSED = booleanPreferencesKey("isCreatePatientCardPassed")

    fun initEncryptedSharedPrefs(context: Context) {
        masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "smartLabPrefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    fun encryptToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun encryptPassword(password: String) {
        sharedPreferences.edit().putString("password", password).apply()
    }

    fun decryptToken(): String {
        return sharedPreferences.getString("token", "") ?: ""
    }

    fun decryptPassword(): String {
        return sharedPreferences.getString("password", "") ?: ""
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

    fun getPatientCard(context: Context): Flow<ProfileRequest>{
        val user = context.dataStore.data.map {
            Gson().fromJson(it[PROFILE] ?: "{}",  ProfileRequest::class.java)
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

    suspend fun saveImageUri(context: Context, imageUri: String): Flow<SaveStatus> {
        return flow {
            context.dataStore.edit {
                it[PROFILE_IMAGE] = imageUri
                emit(SaveStatus.SUCCESS)
            }
        }
    }

    fun getImageUri(context: Context): Flow<String>{
        val imageFile = context.dataStore.data.map {
            it[PROFILE_IMAGE] ?: ""
        }
        return imageFile
    }
}