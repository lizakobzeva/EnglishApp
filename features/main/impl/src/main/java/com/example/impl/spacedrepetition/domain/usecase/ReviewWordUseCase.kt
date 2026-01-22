package com.example.impl.spacedrepetition.domain.usecase

import com.example.impl.spacedrepetition.data.entity.SpacedRepetitionEntity
import com.example.impl.spacedrepetition.data.repository.SpacedRepetitionRepository
import com.example.impl.spacedrepetition.domain.algorithm.SM2Algorithm

/**
 * UseCase для обработки повторения слова
 * @param quality оценка от 0 до 5 (0 = совсем не помню, 5 = отлично помню)
 */
class ReviewWordUseCase(
    private val repository: SpacedRepetitionRepository
) {
    suspend fun execute(wordId: Long, quality: Int) {
        require(quality in 0..5) { "Quality must be between 0 and 5" }
        
        val entity = repository.getByWordId(wordId)
            ?: throw IllegalStateException("Word $wordId not found in spaced repetition system")
        
        // Рассчитываем следующее повторение на основе оценки
        val result = SM2Algorithm.calculateNextReview(
            currentStep = entity.currentStep,
            easeFactor = entity.easeFactor,
            quality = quality
        )
        
        // Обновляем entity
        val updatedEntity = entity.copy(
            currentStep = result.currentStep,
            nextReviewDate = result.nextReviewDate,
            interval = result.interval,
            easeFactor = result.easeFactor,
            lastReviewDate = System.currentTimeMillis(),
            consecutiveCorrectAnswers = if (quality >= 3) {
                entity.consecutiveCorrectAnswers + 1
            } else {
                0
            }
        )
        
        repository.update(updatedEntity)
    }
}

