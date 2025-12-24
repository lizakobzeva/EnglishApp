package com.example.impl.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.impl.data.dao.WordDao
import com.example.impl.data.entity.WordEntity

@Database(
    entities = [WordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}

