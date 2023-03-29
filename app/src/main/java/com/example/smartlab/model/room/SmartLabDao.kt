package com.example.smartlab.model.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.smartlab.model.dto.CatalogItem

@Dao
interface SmartLabDao {

    @Query("select * from analyzes")
    fun getAllAnalyzes(): LiveData<List<CatalogItem>>

    @Insert
    fun addAnalyzeToCart(item: CatalogItem)

    @Update
    fun updateAnalyze(item: CatalogItem)

    @Delete
    fun deleteAnalyzeFromCard(item: CatalogItem)
}