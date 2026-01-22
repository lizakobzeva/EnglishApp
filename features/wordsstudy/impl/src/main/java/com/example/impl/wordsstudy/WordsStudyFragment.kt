package com.example.impl.wordsstudy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.example.api.ImageLoader
import com.example.impl.data.database.DatabaseProvider
import com.example.impl.data.repository.WordsRepository
import com.example.impl.spacedrepetition.data.repository.SpacedRepetitionRepository
import com.example.impl.spacedrepetition.domain.usecase.GetWordsForReviewUseCase
import com.example.impl.spacedrepetition.domain.usecase.ReviewWordUseCase
import com.example.impl.spacedrepetition.ui.ReviewViewModel
import com.example.impl.wordsstudy.R
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class WordsStudyFragment: Fragment() {
    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory(requireContext().applicationContext)
    }
    private val imageLoader by inject<ImageLoader<ImageView>>()
    
    private var isAnswerChecked = false
    
    private lateinit var exitButton: ImageView
    private lateinit var scoreText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var translationText: TextView
    private lateinit var wordImage: ImageView
    private lateinit var wordInput: EditText
    private lateinit var checkButton: Button
    private lateinit var nextButton: Button
    private lateinit var correctAnswerText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("WordsStudyFragment", "onCreateView() called")
        val view = inflater.inflate(R.layout.word_studying, container, false)
        Log.d("WordsStudyFragment", "View inflated: ${view != null}")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("WordsStudyFragment", "onViewCreated() called")
        
        exitButton = view.findViewById(R.id.word_studying_check_exit)
        scoreText = view.findViewById(R.id.word_studying_check_score)
        progressBar = view.findViewById(R.id.word_studying_check_progressbutton)
        translationText = view.findViewById(R.id.word_studying_check_titletext)
        wordImage = view.findViewById(R.id.word_studying_check_image)
        wordInput = view.findViewById(R.id.word_studying_check_editWord)
        checkButton = view.findViewById(R.id.word_studying_check_checkbutton)
        nextButton = view.findViewById(R.id.word_studying_check_nextbutton)
        correctAnswerText = view.findViewById(R.id.word_studying_check_correct_answer)
        
        exitButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        
        checkButton.setOnClickListener {
            checkAnswer()
        }
        
        nextButton.setOnClickListener {
            moveToNextWord()
        }
        
        wordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val hasText = s?.toString()?.trim()?.isNotEmpty() == true
                if (hasText && !isAnswerChecked) {
                    checkButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.greenMain)
                } else {
                    checkButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grayButton)
                }
            }
        })
        
        observeViewModel()
        // Загружаем слова после настройки наблюдателей
        viewLifecycleOwner.lifecycleScope.launch {
            reviewViewModel.loadWordsForReview()
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                reviewViewModel.currentWord.collect { wordWithSpacedRepetition ->
                    Log.d("WordsStudyFragment", "currentWord changed: ${wordWithSpacedRepetition?.word?.title ?: "null"}")
                    wordWithSpacedRepetition?.let { word ->
                        Log.d("WordsStudyFragment", "Showing word: ${word.word.title}")
                        showWord(word)
                    } ?: run {
                        // Нет слов для повторения
                        Log.d("WordsStudyFragment", "No words available")
                        translationText.text = "Нет слов для повторения"
                        wordInput.visibility = View.GONE
                        wordImage.visibility = View.GONE
                        checkButton.visibility = View.GONE
                        nextButton.visibility = View.GONE
                    }
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                reviewViewModel.wordsQueue.collect { words ->
                    updateProgress(words.size)
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                reviewViewModel.currentIndex.collect { index ->
                    reviewViewModel.wordsQueue.value.let { words ->
                        updateProgress(words.size)
                    }
                }
            }
        }
    }
    
    private fun showWord(word: com.example.impl.spacedrepetition.domain.model.WordWithSpacedRepetition) {
        Log.d("WordsStudyFragment", "showWord called for: ${word.word.title}")
        translationText.text = word.word.translation
        imageLoader.load(wordImage, word.word.img)
        wordInput.setText("")
        wordInput.isEnabled = true
        wordInput.visibility = View.VISIBLE
        // Устанавливаем серый фон (#F5F5F5)
        wordInput.setBackgroundColor(0xFFF5F5F5.toInt())
        
        isAnswerChecked = false
        checkButton.visibility = View.VISIBLE
        nextButton.visibility = View.GONE
        correctAnswerText.visibility = View.GONE
        wordImage.visibility = View.VISIBLE
        checkButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grayButton)
        
        val queueSize = reviewViewModel.wordsQueue.value.size
        val currentIdx = reviewViewModel.currentIndex.value
        Log.d("WordsStudyFragment", "Updating progress: currentIdx=$currentIdx, queueSize=$queueSize")
        updateProgress(queueSize)
    }
    
    private fun checkAnswer() {
        if (isAnswerChecked) return
        
        val userAnswer = wordInput.text.toString().trim()
        val currentWord = reviewViewModel.currentWord.value ?: return
        val correctWord = currentWord.word.title.trim()
        
        val isCorrect = userAnswer.equals(correctWord, ignoreCase = true)
        
        if (isCorrect) {
            // Правильный ответ - показываем зеленый фон
            wordInput.setBackgroundColor(0xFFC8E6C9.toInt()) // Зеленый светлый
            nextButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.greenMain)
            // Применяем качество 5 (отлично) для правильного ответа
            reviewViewModel.submitAnswer(isCorrect)
        } else {
            // Неправильный ответ - показываем красный фон и правильный ответ
            wordInput.setBackgroundColor(0xFFFFCDD2.toInt()) // Красный светлый
            nextButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
            // Показываем правильный ответ с переносом строки для лучшей видимости
            correctAnswerText.text = "Правильный ответ:\n$correctWord"
            correctAnswerText.visibility = View.VISIBLE
            // Применяем качество 0 (плохо) для неправильного ответа - слово будет повторено сегодня
            reviewViewModel.submitAnswer(isCorrect)
        }
        
        wordInput.isEnabled = false
        isAnswerChecked = true
        checkButton.visibility = View.GONE
        nextButton.visibility = View.VISIBLE
        nextButton.backgroundTintList = if (isCorrect) {
            ContextCompat.getColorStateList(requireContext(), R.color.greenMain)
        } else {
            ContextCompat.getColorStateList(requireContext(), R.color.red)
        }
        
        updateProgress(reviewViewModel.wordsQueue.value.size)
    }
    
    private fun moveToNextWord() {
        reviewViewModel.nextWord()
    }
    
    private fun updateProgress(total: Int) {
        val current = reviewViewModel.currentIndex.value + 1
        scoreText.text = "$current/$total"
        progressBar.max = total
        progressBar.progress = current
    }
}

// ViewModelFactory для ReviewViewModel
class ReviewViewModelFactory(private val applicationContext: android.content.Context) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(applicationContext)
            val wordsRepository = WordsRepository(database.wordDao())
            val spacedRepetitionRepository = SpacedRepetitionRepository(database.spacedRepetitionDao())
            val getWordsForReviewUseCase = GetWordsForReviewUseCase(wordsRepository, spacedRepetitionRepository)
            val reviewWordUseCase = ReviewWordUseCase(spacedRepetitionRepository)
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(getWordsForReviewUseCase, reviewWordUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
