package com.example.impl.spacedrepetition.domain.model

import com.example.impl.data.entity.WordEntity
import com.example.impl.spacedrepetition.data.entity.SpacedRepetitionEntity
import java.util.Calendar

/**
 * Модель, объединяющая слово и данные интервального повторения
 */
data class WordWithSpacedRepetition(
    val word: WordEntity,
    val spacedRepetition: SpacedRepetitionEntity?
) {
    /**
     * Статус слова для отображения
     */
    enum class Status {
        NEW,        // Новое слово (currentStep = 0)
        LEARNING,   // В процессе изучения (currentStep 1-5)
        LEARNED,    // Выучено (currentStep = 6)
        READY       // Готово к повторению (время пришло)
    }
    
    val status: Status
        get() = when {
            spacedRepetition == null -> Status.NEW
            spacedRepetition.currentStep == 0 -> Status.NEW
            spacedRepetition.currentStep == 6 -> Status.LEARNED
            isReadyForReview() -> Status.READY
            else -> Status.LEARNING
        }
    
    /**
     * Проверяет, готово ли слово к повторению
     */
    fun isReadyForReview(): Boolean {
        if (spacedRepetition == null) return false
        if (spacedRepetition.currentStep == 0 || spacedRepetition.currentStep == 6) return false
        return spacedRepetition.nextReviewDate <= System.currentTimeMillis()
    }
    
    /**
     * Возвращает строку с временем до следующего повторения
     */
    fun getTimeUntilReview(): String {
        if (spacedRepetition == null) return "Новое"
        if (spacedRepetition.currentStep == 6) return "Выучено"
        
        val now = System.currentTimeMillis()
        val reviewTime = spacedRepetition.nextReviewDate
        
        if (reviewTime <= now) {
            return "Сегодня"
        }
        
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = now
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val todayYear = calendar.get(Calendar.YEAR)
        
        calendar.timeInMillis = reviewTime
        val reviewDay = calendar.get(Calendar.DAY_OF_YEAR)
        val reviewYear = calendar.get(Calendar.YEAR)
        
        val daysDiff = if (reviewYear == todayYear) {
            reviewDay - today
        } else {
            val daysInYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
            (daysInYear - today) + reviewDay
        }
        
        return when (daysDiff) {
            0 -> "Сегодня"
            1 -> "Завтра"
            else -> "Через $daysDiff ${getDaysWord(daysDiff)}"
        }
    }
    
    private fun getDaysWord(days: Int): String {
        val lastDigit = days % 10
        val lastTwoDigits = days % 100
        
        return when {
            lastTwoDigits in 11..14 -> "дней"
            lastDigit == 1 -> "день"
            lastDigit in 2..4 -> "дня"
            else -> "дней"
        }
    }
}

