package com.example.smartlab.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartlab.model.dto.CatalogItem

@Database(entities = [CatalogItem::class], version = 1)
abstract class SmartLabDatabase : RoomDatabase() {

    abstract fun getDao(): SmartLabDao

    companion object {
        private var db: SmartLabDatabase? = null

        fun getDb(context: Context): SmartLabDatabase {
            if (db == null) {
                db = Room.databaseBuilder(context, SmartLabDatabase::class.java, "smartLabDatabase")
                    .build()
            }
            return db!!
        }
    }
}
