package com.example.impl.spacedrepetition.domain.usecase

import android.util.Log
import com.example.impl.data.entity.WordEntity
import com.example.impl.data.repository.WordsRepository
import com.example.impl.spacedrepetition.data.repository.SpacedRepetitionRepository
import com.example.impl.spacedrepetition.domain.model.WordWithSpacedRepetition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * UseCase для получения слов по статусу (Новые/Обучаемые/Выученные)
 */
class GetWordsByStatusUseCase(
    private val wordsRepository: WordsRepository,
    private val spacedRepetitionRepository: SpacedRepetitionRepository
) {
    fun getNewWords(): Flow<List<WordWithSpacedRepetition>> {
        return combine(
            wordsRepository.getAllWords(),
            spacedRepetitionRepository.getAll()
        ) { words, spacedRepetitions ->
            Log.d("GetWordsByStatusUseCase", "Total words: ${words.size}, Total spaced repetitions: ${spacedRepetitions.size}")
            val spacedRepetitionMap = spacedRepetitions.associateBy { it.wordId }
            
            val result = words
                .map { word ->
                    val spacedRepetition = spacedRepetitionMap[word.id]
                    WordWithSpacedRepetition(word, spacedRepetition)
                }
                .filter { it.status == WordWithSpacedRepetition.Status.NEW }
            
            Log.d("GetWordsByStatusUseCase", "New words count: ${result.size}")
            result
        }
    }
    
    fun getLearningWords(): Flow<List<WordWithSpacedRepetition>> {
        return combine(
            wordsRepository.getAllWords(),
            spacedRepetitionRepository.getAll()
        ) { words, spacedRepetitions ->
            val spacedRepetitionMap = spacedRepetitions.associateBy { it.wordId }
            
            val result = words
                .map { word ->
                    val spacedRepetition = spacedRepetitionMap[word.id]
                    WordWithSpacedRepetition(word, spacedRepetition)
                }
                .filter { it.status == WordWithSpacedRepetition.Status.LEARNING || 
                          it.status == WordWithSpacedRepetition.Status.READY }
            
            Log.d("GetWordsByStatusUseCase", "Learning words count: ${result.size}")
            result
        }
    }
    
    fun getLearnedWords(): Flow<List<WordWithSpacedRepetition>> {
        return combine(
            wordsRepository.getAllWords(),
            spacedRepetitionRepository.getAll()
        ) { words, spacedRepetitions ->
            val spacedRepetitionMap = spacedRepetitions.associateBy { it.wordId }
            
            val result = words
                .map { word ->
                    val spacedRepetition = spacedRepetitionMap[word.id]
                    WordWithSpacedRepetition(word, spacedRepetition)
                }
                .filter { it.status == WordWithSpacedRepetition.Status.LEARNED }
            
            Log.d("GetWordsByStatusUseCase", "Learned words count: ${result.size}")
            result
        }
    }
    
    fun getAllWords(): Flow<List<WordWithSpacedRepetition>> {
        return combine(
            wordsRepository.getAllWords(),
            spacedRepetitionRepository.getAll()
        ) { words, spacedRepetitions ->
            val spacedRepetitionMap = spacedRepetitions.associateBy { it.wordId }
            
            words.map { word ->
                val spacedRepetition = spacedRepetitionMap[word.id]
                WordWithSpacedRepetition(word, spacedRepetition)
            }
        }
    }
}

