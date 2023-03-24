package com.example.smartlab.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smartlab.model.api.SmartLabClient
import com.example.smartlab.model.dto.CatalogItem
import com.example.smartlab.model.dto.NewsItem
import kotlinx.coroutines.launch

class AnalyzesViewModel(private val app: Application) : AndroidViewModel(app) {

    private var _news = MutableLiveData<List<NewsItem>>()
    val news = _news

    private var _catalog = MutableLiveData<List<CatalogItem>>()
    val catalog = _catalog

    private val _categories: MutableLiveData<HashSet<String>> = MutableLiveData()
    val categories: LiveData<HashSet<String>> = _categories


//    private val _categories = MutableLiveData<List<String>>()
//    val categories = _categories

    fun getNews() {
        viewModelScope.launch {
            val response = SmartLabClient.retrofit.getNews()
            if (response.isSuccessful) {
                if (response.body() != null) {
                    _news.value = response.body()
                }
            }
        }
    }

    fun getCatalog() {
        viewModelScope.launch {
            val response = SmartLabClient.retrofit.getCatalog()
            if (response.isSuccessful) {
                if (response.body() != null) {
                    _catalog.value = response.body()
                    val currentCategories = java.util.HashSet<String>()
                    response.body()!!.forEach {
                        currentCategories.add(it.category)
                    }
                    _categories.value = currentCategories
                }
            }
        }
    }
}