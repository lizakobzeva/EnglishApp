package com.example.impl.spacedrepetition.domain.algorithm

import java.util.Calendar

/**
 * Алгоритм SM-2 для интервального повторения
 * Адаптирован под требования:
 * - Первое повторение: через 1 день
 * - Второе повторение: через 3 дня
 * - Третье повторение: через 7 дней
 * - Четвертое повторение: через 14 дней
 * - Пятое повторение: через 30 дней
 * - После пятого повторения слово считается выученным
 */
object SM2Algorithm {
    // Фиксированные интервалы для первых 5 этапов
    private val FIXED_INTERVALS = listOf(1, 3, 7, 14, 30)
    
    // Минимальный easeFactor
    private const val MIN_EASE_FACTOR = 1.3
    
    // Максимальный easeFactor
    private const val MAX_EASE_FACTOR = 2.5
    
    /**
     * Рассчитывает следующее состояние слова на основе оценки (0-5)
     * @param currentStep текущий этап (0-5)
     * @param easeFactor текущий коэффициент легкости
     * @param quality оценка от 0 до 5 (на основе количества ошибок)
     * @return результат расчета с новыми значениями
     */
    fun calculateNextReview(
        currentStep: Int,
        easeFactor: Double,
        quality: Int // 0 = совсем не помню, 5 = отлично помню
    ): ReviewResult {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = now
        
        // Если оценка низкая (0-2), устанавливаем повторение на сегодня
        if (quality <= 2) {
            val newEaseFactor = (easeFactor - 0.2).coerceAtLeast(MIN_EASE_FACTOR)
            // Устанавливаем nextReviewDate на начало сегодняшнего дня (00:00:00)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val todayStart = calendar.timeInMillis
            return ReviewResult(
                currentStep = if (quality == 0) 1 else currentStep, // При качестве 0 сбрасываем на этап 1
                nextReviewDate = todayStart, // Повторение сегодня
                interval = 0, // Интервал 0 дней (сегодня)
                easeFactor = newEaseFactor,
                consecutiveCorrectAnswers = 0
            )
        }
        
        // Если это первое повторение (currentStep = 0)
        if (currentStep == 0) {
            // Всегда переходим на этап 1 после первого повторения
            calendar.add(Calendar.DAY_OF_MONTH, FIXED_INTERVALS[0])
            val newEaseFactor = when {
                quality >= 4 -> (easeFactor + 0.1).coerceAtMost(MAX_EASE_FACTOR)
                quality == 3 -> easeFactor
                else -> (easeFactor - 0.15).coerceAtLeast(MIN_EASE_FACTOR)
            }
            return ReviewResult(
                currentStep = 1,
                nextReviewDate = calendar.timeInMillis,
                interval = FIXED_INTERVALS[0],
                easeFactor = newEaseFactor,
                consecutiveCorrectAnswers = if (quality >= 3) 1 else 0
            )
        }
        
        // Если уже прошли 5 этапов, слово выучено
        if (currentStep >= 5) {
            return ReviewResult(
                currentStep = 6, // выучено
                nextReviewDate = Long.MAX_VALUE, // больше не повторяем
                interval = 0,
                easeFactor = easeFactor,
                consecutiveCorrectAnswers = 0
            )
        }
        
        
        // Для этапов 1-4 используем фиксированные интервалы
        val nextStep = currentStep + 1
        val nextInterval = if (nextStep <= 5) {
            FIXED_INTERVALS[nextStep - 1]
        } else {
            30 // fallback
        }
        
        // Обновляем easeFactor на основе качества
        val newEaseFactor = when {
            quality >= 4 -> (easeFactor + 0.1).coerceAtMost(MAX_EASE_FACTOR)
            quality == 3 -> easeFactor
            else -> (easeFactor - 0.15).coerceAtLeast(MIN_EASE_FACTOR)
        }
        
        calendar.add(Calendar.DAY_OF_MONTH, nextInterval)
        
        val finalStep = if (nextStep >= 5) 6 else nextStep
        
        return ReviewResult(
            currentStep = finalStep,
            nextReviewDate = calendar.timeInMillis,
            interval = nextInterval,
            easeFactor = newEaseFactor,
            consecutiveCorrectAnswers = if (quality >= 3) {
                // Увеличиваем счетчик правильных ответов подряд
                // (в реальности нужно хранить это в entity, но для упрощения используем 1)
                1
            } else {
                0
            }
        )
    }
    
    /**
     * Конвертирует количество ошибок в оценку качества (0-5)
     * @param errorCount количество ошибок при вводе ответа
     * @return оценка от 0 до 5
     */
    fun calculateQualityFromErrors(errorCount: Int): Int {
        return when {
            errorCount == 0 -> 5 // Отлично - без ошибок
            errorCount == 1 -> 4 // Хорошо - одна ошибка
            errorCount == 2 -> 3 // Удовлетворительно - две ошибки
            errorCount == 3 -> 2 // Плохо - три ошибки
            errorCount == 4 -> 1 // Очень плохо - четыре ошибки
            else -> 0 // Очень плохо - много ошибок
        }
    }
    
    /**
     * Результат расчета следующего повторения
     */
    data class ReviewResult(
        val currentStep: Int,
        val nextReviewDate: Long,
        val interval: Int,
        val easeFactor: Double,
        val consecutiveCorrectAnswers: Int
    )
}

