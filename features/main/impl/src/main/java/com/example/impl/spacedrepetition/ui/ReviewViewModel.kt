package com.example.impl.spacedrepetition.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.impl.data.entity.WordEntity
import com.example.impl.spacedrepetition.domain.model.WordWithSpacedRepetition
import com.example.impl.spacedrepetition.domain.usecase.GetWordsForReviewUseCase
import com.example.impl.spacedrepetition.domain.usecase.ReviewWordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val getWordsForReviewUseCase: GetWordsForReviewUseCase,
    private val reviewWordUseCase: ReviewWordUseCase
) : ViewModel() {
    
    private val _currentWord = MutableStateFlow<WordWithSpacedRepetition?>(null)
    val currentWord: StateFlow<WordWithSpacedRepetition?> = _currentWord
    
    private val _wordsQueue = MutableStateFlow<List<WordWithSpacedRepetition>>(emptyList())
    val wordsQueue: StateFlow<List<WordWithSpacedRepetition>> = _wordsQueue
    
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex
    
    private val _isAnswerChecked = MutableStateFlow(false)
    val isAnswerChecked: StateFlow<Boolean> = _isAnswerChecked
    
    private val _isAnswerShown = MutableStateFlow(false)
    val isAnswerShown: StateFlow<Boolean> = _isAnswerShown
    
    private val _userAnswer = MutableStateFlow("")
    val userAnswer: StateFlow<String> = _userAnswer
    
    init {
        Log.d("ReviewViewModel", "ReviewViewModel initialized")
        // Не загружаем слова в init, чтобы избежать двойной загрузки
    }
    
    fun loadWordsForReview() {
        viewModelScope.launch {
            Log.d("ReviewViewModel", "Loading words for review...")
            val words = getWordsForReviewUseCase.execute(limit = 20)
            Log.d("ReviewViewModel", "Loaded ${words.size} words for review")
            
            if (words.isEmpty()) {
                Log.w("ReviewViewModel", "No words found for review!")
                _currentWord.value = null
                _wordsQueue.value = emptyList()
                _currentIndex.value = 0
                return@launch
            }
            
            _wordsQueue.value = words
            _currentIndex.value = 0
            _currentWord.value = words[0]
            _isAnswerChecked.value = false
            Log.d("ReviewViewModel", "Current word set to first word: ${words[0].word.title}, index: 0")
        }
    }
    
    /**
     * Проверяет ответ (простая проверка, как в оригинале)
     * @return true если ответ правильный, false если неправильный
     */
    fun checkAnswer(userInput: String, correctAnswer: String): Boolean {
        val normalizedInput = userInput.trim().lowercase()
        val normalizedCorrect = correctAnswer.trim().lowercase()
        return normalizedInput == normalizedCorrect
    }
    
    /**
     * Применяет результат ответа к системе интервального повторения
     * @param isCorrect true если ответ правильный, false если неправильный
     */
    fun submitAnswer(isCorrect: Boolean) {
        viewModelScope.launch {
            val word = _currentWord.value ?: return@launch
            
            val quality = if (isCorrect) {
                5 // Правильный ответ - качество 5 (отлично)
            } else {
                0 // Неправильный ответ - качество 0 (плохо), будет повторено сегодня
            }
            
            Log.d("ReviewViewModel", "Submitting answer: isCorrect=$isCorrect, quality=$quality for word: ${word.word.title}")
            
            // Применяем алгоритм интервального повторения с оценкой
            reviewWordUseCase.execute(word.word.id, quality)
            
            _isAnswerChecked.value = true
        }
    }
    
    fun submitQuality(quality: Int) {
        viewModelScope.launch {
            val word = _currentWord.value ?: return@launch
            
            Log.d("ReviewViewModel", "Submitting quality: $quality for word: ${word.word.title}")
            
            // Применяем алгоритм интервального повторения с оценкой
            reviewWordUseCase.execute(word.word.id, quality)
            
            _isAnswerChecked.value = true
        }
    }
    
    fun nextWord() {
        val currentIdx = _currentIndex.value
        val queue = _wordsQueue.value
        
        Log.d("ReviewViewModel", "nextWord called: currentIdx=$currentIdx, queueSize=${queue.size}")
        
        if (currentIdx < queue.size - 1) {
            val nextIdx = currentIdx + 1
            _currentIndex.value = nextIdx
            _currentWord.value = queue[nextIdx]
            _isAnswerChecked.value = false
            _isAnswerShown.value = false
            _userAnswer.value = ""
            Log.d("ReviewViewModel", "Moved to next word: index=$nextIdx, word=${queue[nextIdx].word.title}")
        } else {
            // Все слова пройдены, загружаем новые
            Log.d("ReviewViewModel", "All words completed, loading new batch...")
            viewModelScope.launch {
                val newWords = getWordsForReviewUseCase.execute(limit = 20)
                Log.d("ReviewViewModel", "Loaded ${newWords.size} new words")
                if (newWords.isNotEmpty()) {
                    _wordsQueue.value = newWords
                    _currentIndex.value = 0
                    _currentWord.value = newWords[0]
                    _isAnswerChecked.value = false
                    _isAnswerShown.value = false
                    _userAnswer.value = ""
                    Log.d("ReviewViewModel", "New batch started with word: ${newWords[0].word.title}")
                } else {
                    // Нет больше слов для повторения
                    Log.w("ReviewViewModel", "No more words available for review")
                    _currentWord.value = null
                }
            }
        }
    }
    
    fun resetCurrentWord() {
        _isAnswerChecked.value = false
        _isAnswerShown.value = false
        _userAnswer.value = ""
    }
}

