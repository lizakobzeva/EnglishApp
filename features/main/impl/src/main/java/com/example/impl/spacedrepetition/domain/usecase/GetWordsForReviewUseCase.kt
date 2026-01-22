package com.example.impl.spacedrepetition.domain.usecase

import android.util.Log
import com.example.impl.data.entity.WordEntity
import com.example.impl.data.repository.WordsRepository
import com.example.impl.spacedrepetition.data.repository.SpacedRepetitionRepository
import com.example.impl.spacedrepetition.domain.model.WordWithSpacedRepetition

/**
 * UseCase для получения слов, готовых к повторению
 */
class GetWordsForReviewUseCase(
    private val wordsRepository: WordsRepository,
    private val spacedRepetitionRepository: SpacedRepetitionRepository
) {
    suspend fun execute(limit: Int = Int.MAX_VALUE): List<WordWithSpacedRepetition> {
        // Вычисляем конец сегодняшнего дня (23:59:59.999)
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        val endOfToday = calendar.timeInMillis
        
        Log.d("GetWordsForReviewUseCase", "Getting words for review, endOfToday: $endOfToday, limit: $limit")
        val spacedRepetitions = spacedRepetitionRepository.getWordsForReview(endOfToday)
        Log.d("GetWordsForReviewUseCase", "Found ${spacedRepetitions.size} spaced repetition entries")
        val limitedRepetitions = spacedRepetitions.take(limit)
        
        val wordIds = limitedRepetitions.map { it.wordId }
        Log.d("GetWordsForReviewUseCase", "Word IDs to fetch: $wordIds")
        val words = wordIds.mapNotNull { wordId ->
            wordsRepository.getWordById(wordId)
        }
        Log.d("GetWordsForReviewUseCase", "Fetched ${words.size} words from repository")
        
        val wordsMap = words.associateBy { it.id }
        val spacedRepetitionMap = limitedRepetitions.associateBy { it.wordId }
        
        val result = words
            .map { word ->
                val spacedRepetition = spacedRepetitionMap[word.id]
                WordWithSpacedRepetition(word, spacedRepetition)
            }
            .sortedBy { it.spacedRepetition?.nextReviewDate ?: Long.MAX_VALUE }
        
        Log.d("GetWordsForReviewUseCase", "Returning ${result.size} words for review")
        return result
    }
    
    suspend fun getCount(): Int {
        // Вычисляем конец сегодняшнего дня (23:59:59.999)
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        val endOfToday = calendar.timeInMillis
        return spacedRepetitionRepository.getWordsForReviewCount(endOfToday)
    }
}

