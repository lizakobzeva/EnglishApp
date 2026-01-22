package com.example.impl.spacedrepetition.notification

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReviewNotificationScheduler {
    private const val WORK_NAME = "review_notification_work"
    private const val PREFS_NAME = "review_notification_prefs"
    private const val KEY_HOUR = "notification_hour"
    private const val KEY_MINUTE = "notification_minute"
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun getNotificationTime(context: Context): Pair<Int, Int> {
        val prefs = getPreferences(context)
        val hour = prefs.getInt(KEY_HOUR, 9)
        val minute = prefs.getInt(KEY_MINUTE, 0)
        return Pair(hour, minute)
    }
    
    fun setNotificationTime(context: Context, hour: Int, minute: Int) {
        val prefs = getPreferences(context)
        prefs.edit()
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()
        
        // Перепланируем уведомления с новым временем
        scheduleDailyNotifications(context, hour, minute)
    }
    
    fun scheduleDailyNotifications(context: Context, hour: Int = 9, minute: Int = 0) {
        // Сохраняем настройки
        val prefs = getPreferences(context)
        prefs.edit()
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()
        
        val workManager = WorkManager.getInstance(context)
        
        // Создаем ограничения
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()
        
        // Вычисляем задержку до первого запуска
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        // Если время уже прошло сегодня, планируем на завтра
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        val initialDelay = calendar.timeInMillis - now
        
        // Создаем периодическую задачу (каждый день)
        val workRequest = PeriodicWorkRequestBuilder<ReviewNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()
        
        // Планируем работу (заменяем существующую, если есть)
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    fun cancelNotifications(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(WORK_NAME)
    }
}

