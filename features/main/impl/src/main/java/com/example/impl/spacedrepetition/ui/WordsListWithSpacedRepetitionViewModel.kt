package com.example.impl.spacedrepetition.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.impl.data.repository.WordsRepository
import com.example.impl.spacedrepetition.data.repository.SpacedRepetitionRepository
import com.example.impl.spacedrepetition.domain.model.WordWithSpacedRepetition
import com.example.impl.spacedrepetition.domain.usecase.GetWordsByStatusUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class WordsListWithSpacedRepetitionViewModel(
    private val getWordsByStatusUseCase: GetWordsByStatusUseCase
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    
    val newWords: Flow<List<WordWithSpacedRepetition>> = combine(
        getWordsByStatusUseCase.getNewWords(),
        searchQuery
    ) { words, query ->
        Log.d("WordsListViewModel", "New words count: ${words.size}")
        filterWords(words, query)
    }
    
    val learningWords: Flow<List<WordWithSpacedRepetition>> = combine(
        getWordsByStatusUseCase.getLearningWords(),
        searchQuery
    ) { words, query ->
        Log.d("WordsListViewModel", "Learning words count: ${words.size}")
        filterWords(words, query)
    }
    
    val learnedWords: Flow<List<WordWithSpacedRepetition>> = combine(
        getWordsByStatusUseCase.getLearnedWords(),
        searchQuery
    ) { words, query ->
        Log.d("WordsListViewModel", "Learned words count: ${words.size}")
        filterWords(words, query)
    }
    
    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }
    
    private fun filterWords(words: List<WordWithSpacedRepetition>, query: String): List<WordWithSpacedRepetition> {
        if (query.isBlank()) {
            return words
        }
        return words.filter { word ->
            word.word.title.contains(query, ignoreCase = true) ||
            word.word.translation.contains(query, ignoreCase = true)
        }
    }
}

