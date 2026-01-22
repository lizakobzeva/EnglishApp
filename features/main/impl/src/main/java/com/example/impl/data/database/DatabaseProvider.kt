package com.example.impl.data.database

import android.content.Context
import android.util.Log
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null
    
    fun getDatabase(context: Context): AppDatabase {
        Log.d("DatabaseProvider", "getDatabase() called, INSTANCE: ${INSTANCE != null}")
        return INSTANCE ?: synchronized(this) {
            Log.d("DatabaseProvider", "Creating new database instance")
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "english_app_database"
            )
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build()
            INSTANCE = instance
            Log.d("DatabaseProvider", "Database instance created and cached")
            instance
        }
    }
}


