package com.example.impl.wordsstudy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.api.ImageLoader
import com.example.impl.data.entity.WordEntity
import com.example.impl.ui.viewmodel.WordsViewModel
import com.example.impl.words_list.WordsListFragment
import com.example.impl.words_list.WordsListViewModelFactory
import com.example.impl.wordsstudy.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class WordsStudyFragment: Fragment() {
    private val viewModel: WordsViewModel by viewModels {
        WordsListViewModelFactory(requireContext().applicationContext)
    }
    private val imageLoader by inject<ImageLoader<ImageView>>()
    
    private var wordsList: List<WordEntity> = emptyList()
    private var currentWordIndex = 0
    private var correctAnswers = 0
    private var incorrectAnswers = 0
    private var isAnswerChecked = false
    
    private lateinit var exitButton: ImageView
    private lateinit var scoreText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var translationText: TextView
    private lateinit var wordImage: ImageView
    private lateinit var wordInput: EditText
    private lateinit var checkButton: Button
    private lateinit var nextButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.word_studying, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        exitButton = view.findViewById(R.id.word_studying_exit)
        scoreText = view.findViewById(R.id.word_studying_score)
        progressBar = view.findViewById(R.id.word_studying_progressbutton)
        translationText = view.findViewById(R.id.word_studying_titletext)
        wordImage = view.findViewById(R.id.word_studying_image)
        wordInput = view.findViewById(R.id.word_studying_editWord)
        checkButton = view.findViewById(R.id.word_studying_checkbutton)
        nextButton = view.findViewById(R.id.word_studying_nextbutton)
        
        exitButton.setOnClickListener {
            parentFragmentManager.popBackStack()
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
                    checkButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                } else {
                    checkButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grayButton)
                }
            }
        })
        
        loadWords()
    }
    
    private fun loadWords() {
        viewLifecycleOwner.lifecycleScope.launch {
            wordsList = viewModel.words.first().shuffled()
            if (wordsList.isEmpty()) {
                parentFragmentManager.popBackStack()
                return@launch
            }
            currentWordIndex = 0
            correctAnswers = 0
            incorrectAnswers = 0
            showCurrentWord()
        }
    }
    
    private fun showCurrentWord() {
        if (currentWordIndex >= wordsList.size) {
            showResults()
            return
        }
        
        val currentWord = wordsList[currentWordIndex]
        translationText.text = currentWord.translation
        imageLoader.load(wordImage, currentWord.img)
        wordInput.setText("")
        wordInput.isEnabled = true
        wordInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_gray_rounded)
        
        isAnswerChecked = false
        checkButton.visibility = View.VISIBLE
        nextButton.visibility = View.GONE
        checkButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grayButton)
        
        updateProgress()
    }
    
    private fun checkAnswer() {
        if (isAnswerChecked) return
        
        val userAnswer = wordInput.text.toString().trim()
        val correctWord = wordsList[currentWordIndex].title.trim()
        
        val isCorrect = userAnswer.equals(correctWord, ignoreCase = true)
        
        if (isCorrect) {
            correctAnswers++
            wordInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_green)
            nextButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
        } else {
            incorrectAnswers++
            wordInput.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_red)
            nextButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
            wordInput.setText(correctWord)
        }
        
        wordInput.isEnabled = false
        isAnswerChecked = true
        checkButton.visibility = View.GONE
        nextButton.visibility = View.VISIBLE
        
        updateProgress()
    }
    
    private fun moveToNextWord() {
        currentWordIndex++
        showCurrentWord()
    }
    
    private fun updateProgress() {
        val total = wordsList.size
        val current = currentWordIndex + 1
        scoreText.text = "$current/$total"
        progressBar.max = total
        progressBar.progress = current
    }
    
    private fun showResults() {
        val containerId = requireActivity().resources.getIdentifier("main", "id", requireActivity().packageName)
        val resultsFragment = StudyResultsFragment.newInstance(correctAnswers, incorrectAnswers)
        parentFragmentManager.beginTransaction()
            .replace(containerId, resultsFragment)
            .addToBackStack(null)
            .commit()
    }
}
