package com.example.impl.words_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.example.api.ImageLoader
import com.example.api.WordConstants.ACTION_EDITWORD
import com.example.api.WordConstants.ACTION_STUDY
import com.example.impl.R
import com.example.impl.spacedrepetition.domain.model.WordWithSpacedRepetition
import com.example.impl.spacedrepetition.ui.WordsListWithSpacedRepetitionViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class WordsListTabFragment : Fragment() {
    private var viewModel: WordsListWithSpacedRepetitionViewModel? = null
    private val imageLoader by inject<ImageLoader<ImageView>>()
    
    private var wordsFlow: Flow<List<WordWithSpacedRepetition>>? = null
    
    fun setWordsFlow(flow: Flow<List<WordWithSpacedRepetition>>) {
        wordsFlow = flow
    }
    
    fun setViewModel(viewModel: WordsListWithSpacedRepetitionViewModel) {
        this.viewModel = viewModel
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.words_list_tab, container, false)
    }
    
    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val container = view.findViewById<ViewGroup>(R.id.words_list)
        val flow = wordsFlow ?: return
        
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { words ->
                    container.removeAllViews()
                    
                    if (words.isEmpty()) {
                        // Показываем сообщение, если список пуст
                        val emptyView = layoutInflater.inflate(android.R.layout.simple_list_item_1, container, false)
                        val emptyText = emptyView.findViewById<TextView>(android.R.id.text1)
                        emptyText.text = "Нет слов для отображения"
                        emptyText.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        container.addView(emptyView)
                        return@collect
                    }
                    
                    words.forEach { wordWithSpacedRepetition ->
                        val card = layoutInflater.inflate(R.layout.word, container, false)
                        val title = card.findViewById<TextView>(R.id.word_title)
                        val translation = card.findViewById<TextView>(R.id.word_translation)
                        val pronunciation = card.findViewById<TextView>(R.id.word_pronunciation)
                        val image = card.findViewById<ImageView>(R.id.image_word)
                        val reviewTimer = card.findViewById<TextView>(R.id.word_review_timer)
                        
                        container.addView(card)
                        
                        title.text = wordWithSpacedRepetition.word.title
                        translation.text = wordWithSpacedRepetition.word.translation
                        pronunciation.text = wordWithSpacedRepetition.word.pronunciation
                        imageLoader.load(image, wordWithSpacedRepetition.word.img)
                        
                        // Показываем таймер для обучаемых слов
                        when (wordWithSpacedRepetition.status) {
                            WordWithSpacedRepetition.Status.NEW -> {
                                reviewTimer.visibility = View.GONE
                            }
                            WordWithSpacedRepetition.Status.LEARNING,
                            WordWithSpacedRepetition.Status.READY -> {
                                reviewTimer.visibility = View.VISIBLE
                                reviewTimer.text = wordWithSpacedRepetition.getTimeUntilReview()
                                
                                // Если слово готово к повторению, выделяем его
                                if (wordWithSpacedRepetition.status == WordWithSpacedRepetition.Status.READY) {
                                    reviewTimer.setTextColor(ContextCompat.getColor(requireContext(), R.color.greenMain))
                                } else {
                                    reviewTimer.setTextColor(ContextCompat.getColor(requireContext(), R.color.textSecondaryColor))
                                }
                            }
                            WordWithSpacedRepetition.Status.LEARNED -> {
                                reviewTimer.visibility = View.VISIBLE
                                reviewTimer.text = "Выучено"
                                reviewTimer.setTextColor(ContextCompat.getColor(requireContext(), R.color.textSecondaryColor))
                            }
                        }
                        
                        card.setOnClickListener {
                            // Если слово готово к повторению, открываем экран повторения
                            if (wordWithSpacedRepetition.status == WordWithSpacedRepetition.Status.READY) {
                                Intent(Intent.ACTION_VIEW).apply {
                                    data = ACTION_STUDY.toUri()
                                    flags += Intent.FLAG_ACTIVITY_SINGLE_TOP
                                }.also(::startActivity)
                            } else {
                                // Иначе открываем редактирование
                                Intent(Intent.ACTION_VIEW).apply {
                                    data = ACTION_EDITWORD.toUri()
                                    flags += Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    putExtra("word_id", wordWithSpacedRepetition.word.id.toString())
                                }.also(::startActivity)
                            }
                        }
                    }
                }
            }
        }
    }
}

