package com.example.englishapp

import android.app.Application
import android.util.Log
import com.example.impl.data.database.DatabaseProvider
import com.example.impl.data.database.DatabaseInitializer
import com.example.impl.spacedrepetition.notification.ReviewNotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class EnglishAppApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    override fun onCreate() {
        super.onCreate()
        
        Log.d("EnglishAppApplication", "Application onCreate() called")
        
        // Инициализация базы данных
        val database = DatabaseProvider.getDatabase(this)
        Log.d("EnglishAppApplication", "Database obtained: ${database != null}")
        
        // Первичное заполнение БД
        applicationScope.launch {
            Log.d("EnglishAppApplication", "Starting database initialization...")
            DatabaseInitializer.initialize(database)
            Log.d("EnglishAppApplication", "Database initialization completed")
        }
        
        // Планируем ежедневные уведомления (загружаем сохраненное время или используем 9:00 по умолчанию)
        val (hour, minute) = ReviewNotificationScheduler.getNotificationTime(this)
        ReviewNotificationScheduler.scheduleDailyNotifications(this, hour, minute)
        Log.d("EnglishAppApplication", "Notifications scheduled for $hour:$minute")
    }
}


