package com.example.impl.words_list

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import com.example.api.ImageLoader
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.example.api.WordConstants.ACTION_ADDWORD
import com.example.api.WordConstants.ACTION_EDITWORD
import com.example.impl.R
import com.example.impl.ui.viewmodel.WordsViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class WordsListFragment: Fragment() {
    private val viewModel: WordsViewModel by viewModels {
        WordsListViewModelFactory(requireContext().applicationContext)
    }
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
        val searchButton = view.findViewById<ImageView>(R.id.btn_search)
        val searchInput = view.findViewById<EditText>(R.id.search_input)
        val addWordButton = view.findViewById<ImageButton>(R.id.btn_add)

        // Обработчик кнопки поиска
        if (searchButton == null) {
            Log.e("WordsListFragment", "Search button is null!")
        }
        if (searchInput == null) {
            Log.e("WordsListFragment", "Search input is null!")
        }
        
        searchButton?.let { button ->
            button.isClickable = true
            button.isFocusable = true
            button.setOnClickListener {
                Log.d("WordsListFragment", "Search button clicked")
                searchInput?.let { input ->
                    if (input.visibility == View.GONE) {
                        input.visibility = View.VISIBLE
                        input.requestFocus()
                        Log.d("WordsListFragment", "Search input shown")
                    } else {
                        input.visibility = View.GONE
                        input.setText("")
                        viewModel.setSearchQuery("")
                        Log.d("WordsListFragment", "Search input hidden")
                    }
                } ?: Log.e("WordsListFragment", "Search input is null in click handler")
            }
        } ?: Log.e("WordsListFragment", "Search button is null, cannot set click listener")

        // Обработчик ввода текста в поле поиска
        searchInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
        })

        addWordButton.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply {
                data = ACTION_ADDWORD.toUri()
                flags += Intent.FLAG_ACTIVITY_SINGLE_TOP
            }.also(::startActivity)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.words.collect { words ->
                    container.removeAllViews()
                    words.forEach { word ->
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
                            Intent(Intent.ACTION_VIEW).apply {
                                data = ACTION_EDITWORD.toUri()
                                flags += Intent.FLAG_ACTIVITY_SINGLE_TOP
                                putExtra("word_id", word.id.toString())
                            }.also(::startActivity)
//
//                val editWordFragment = EditWordFragment()
//
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.main, editWordFragment)
//                    .addToBackStack("com/example/impl/words_list")
//                    .commit()
                        }
                    }
                }
            }
        }
    }


}