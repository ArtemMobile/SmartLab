package com.example.smartlab.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smartlab")


fun Context.dpToPx(dp: Double): Int = (dp * resources.displayMetrics.density).toInt()