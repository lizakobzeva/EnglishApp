package com.example.englishapp.words_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.api.ImageLoader
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.englishapp.MainActivity
import com.example.englishapp.R
import org.koin.android.ext.android.inject
import kotlin.getValue

class WordsListFragment: Fragment() {
    private val viewModel: WordsListViewModel by viewModels()
    private val imageLoader by inject<ImageLoader<ImageView>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.words_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<ViewGroup>(R.id.words_list)
        viewModel.wordsList().forEach { words ->
            val card = layoutInflater.inflate(R.layout.word, container, false)
            val title = card.findViewById<TextView>(R.id.word_title)
            val translation = card.findViewById<TextView>(R.id.word_translation)
            val pronunciation = card.findViewById<TextView>(R.id.word_pronunciation)
            val image = card.findViewById<ImageView>(R.id.image_word) // <-- ИСПРАВЛЕНО: ищем в card, а не в view
            container.addView(card)

            title.text = words.title
            translation.text = words.translation
            pronunciation.text = words.pronunciation
            imageLoader.load(image, words.img)

            card.setOnClickListener {
                val intent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra("word", words.title)
                }
                startActivity(intent)
            }
        }
    }


}