package com.example.impl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.impl.data.entity.WordEntity
import com.example.impl.data.repository.WordsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class WordsViewModel(private val repository: WordsRepository) : ViewModel() {
    private val allWords: Flow<List<WordEntity>> = repository.getAllWords()
    private val searchQuery = MutableStateFlow("")
    
    val words: Flow<List<WordEntity>> = combine(allWords, searchQuery) { words, query ->
        if (query.isBlank()) {
            words
        } else {
            words.filter { word ->
                word.title.contains(query, ignoreCase = true) ||
                word.translation.contains(query, ignoreCase = true)
            }
        }
    }
    
    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }
    
    suspend fun getWordById(id: Long): WordEntity? {
        return repository.getWordById(id)
    }
    
    fun insertWord(word: WordEntity) {
        viewModelScope.launch {
            repository.insertWord(word)
        }
    }
    
    fun updateWord(word: WordEntity) {
        viewModelScope.launch {
            repository.updateWord(word)
        }
    }
    
    fun deleteWord(word: WordEntity) {
        viewModelScope.launch {
            repository.deleteWord(word)
        }
    }
    
    fun deleteWordById(id: Long) {
        viewModelScope.launch {
            repository.deleteWordById(id)
        }
    }
}

