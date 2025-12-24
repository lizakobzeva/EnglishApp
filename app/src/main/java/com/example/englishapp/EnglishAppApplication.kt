package com.example.englishapp

import android.app.Application
import com.example.impl.data.database.DatabaseProvider
import com.example.impl.data.database.DatabaseInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class EnglishAppApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    override fun onCreate() {
        super.onCreate()
        
        // Инициализация базы данных
        val database = DatabaseProvider.getDatabase(this)
        
        // Первичное заполнение БД
        applicationScope.launch {
            DatabaseInitializer.initialize(database)
        }
    }
}

