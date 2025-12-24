package com.example.englishapp.add_word

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.api.ImageLoader
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.example.englishapp.R
import com.example.impl.data.entity.WordEntity
import com.example.impl.ui.viewmodel.WordsViewModel
import com.example.impl.words_list.WordsListViewModelFactory
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AddWordFragment: Fragment() {
    private val viewModel: WordsViewModel by viewModels {
        WordsListViewModelFactory(requireContext().applicationContext)
    }
    private val imageLoader by inject<ImageLoader<ImageView>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.addwords, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<ViewGroup>(R.id.addwords)

        // Показываем слова из enum AddWords, которые можно добавить в базу данных
        AddWords.entries.forEach { addWord ->
            val card = layoutInflater.inflate(R.layout.addword, container, false)
            val title = card.findViewById<TextView>(R.id.addword_title)
            val translation = card.findViewById<TextView>(R.id.addword_translation)
            val pronunciation = card.findViewById<TextView>(R.id.addword_pronunciation)
            val image = card.findViewById<ImageView>(R.id.addword_image)
            val addButton = card.findViewById<Button>(R.id.addword_addbutton)
            container.addView(card)

            title.text = addWord.title
            translation.text = addWord.translation
            pronunciation.text = addWord.pronunciation
            imageLoader.load(image, addWord.img)

            addButton.setOnClickListener {
                // Добавляем слово в базу данных
                val wordEntity = WordEntity(
                    title = addWord.title,
                    translation = addWord.translation,
                    pronunciation = addWord.pronunciation,
                    img = addWord.img
                )
                viewModel.insertWord(wordEntity)
            }
        }
    }
}