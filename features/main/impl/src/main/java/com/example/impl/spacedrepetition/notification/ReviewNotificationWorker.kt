package com.example.impl.spacedrepetition.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.impl.R
import com.example.impl.data.database.DatabaseProvider
import com.example.impl.data.repository.WordsRepository
import com.example.impl.spacedrepetition.data.repository.SpacedRepetitionRepository
import com.example.impl.spacedrepetition.domain.usecase.GetWordsForReviewUseCase

class ReviewNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val database = DatabaseProvider.getDatabase(applicationContext)
            val wordsRepository = WordsRepository(database.wordDao())
            val spacedRepetitionRepository = SpacedRepetitionRepository(database.spacedRepetitionDao())
            val getWordsForReviewUseCase = GetWordsForReviewUseCase(wordsRepository, spacedRepetitionRepository)
            
            val wordsForReview = getWordsForReviewUseCase.execute(limit = 10)
            
            // Отправляем уведомление только если есть >= 5 слов
            if (wordsForReview.size >= 5) {
                showNotification(wordsForReview)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private fun showNotification(words: List<com.example.impl.spacedrepetition.domain.model.WordWithSpacedRepetition>) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Создаем канал уведомлений для Android O и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Повторение слов",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления о словах, готовых к повторению"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // Формируем текст уведомления
        val firstFiveWords = words.take(5).joinToString(", ") { it.word.title }
        val moreWordsText = if (words.size > 5) "..." else ""
        val notificationText = "Пора повторить слова:\n$firstFiveWords$moreWordsText"
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Пора повторить слова")
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    companion object {
        private const val CHANNEL_ID = "review_notification_channel"
        private const val NOTIFICATION_ID = 1
    }
}

