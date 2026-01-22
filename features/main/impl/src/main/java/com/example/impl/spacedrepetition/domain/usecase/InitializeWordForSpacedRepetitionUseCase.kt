package com.example.impl.spacedrepetition.domain.usecase

import com.example.impl.spacedrepetition.data.entity.SpacedRepetitionEntity
import com.example.impl.spacedrepetition.data.repository.SpacedRepetitionRepository

/**
 * UseCase для инициализации нового слова в системе интервального повторения
 */
class InitializeWordForSpacedRepetitionUseCase(
    private val repository: SpacedRepetitionRepository
) {
    suspend fun execute(wordId: Long) {
        // Проверяем, не существует ли уже запись
        val existing = repository.getByWordId(wordId)
        if (existing != null) {
            return // Уже инициализировано
        }
        
        // Создаем новую запись с начальными значениями
        val entity = SpacedRepetitionEntity(
            wordId = wordId,
            currentStep = 0, // новое слово
            nextReviewDate = 0, // будет установлено при первом повторении
            interval = 0,
            easeFactor = 2.5, // начальный easeFactor
            lastReviewDate = 0,
            consecutiveCorrectAnswers = 0
        )
        
        repository.insert(entity)
    }
}

