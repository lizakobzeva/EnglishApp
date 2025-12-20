package com.example.englishapp.words_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.example.api.ImageLoader
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.englishapp.MainActivity
import com.example.englishapp.R
import com.example.englishapp.add_word.AddWordFragment
import com.example.englishapp.edit_word.EditWordFragment
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

        val addWordButton = view.findViewById<ImageButton>(R.id.btn_add)

        addWordButton.setOnClickListener {
            val addWordFragment = AddWordFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.main, addWordFragment)
                .addToBackStack("words_list")
                .commit()

        }

        viewModel.wordsList().forEach { word ->
            val card = layoutInflater.inflate(R.layout.word, container, false)
            val title = card.findViewById<TextView>(R.id.word_title)
            val translation = card.findViewById<TextView>(R.id.word_translation)
            val pronunciation = card.findViewById<TextView>(R.id.word_pronunciation)
            val image = card.findViewById<ImageView>(R.id.image_word)
            container.addView(card)

            title.text = word.title
            translation.text = word.translation
            pronunciation.text = word.pronunciation
            imageLoader.load(image, word.img)

            card.setOnClickListener {
                val editWordFragment = EditWordFragment()

                parentFragmentManager.beginTransaction()
                    .replace(R.id.main, editWordFragment)
                    .addToBackStack("words_list")
                    .commit()
            }
        }
    }


}