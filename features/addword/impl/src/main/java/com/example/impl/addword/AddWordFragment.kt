package com.example.impl.addword

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
import com.example.api.EditWordApi
import com.example.api.ImageLoader
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.impl.addword.R
import com.example.impl.data.entity.WordEntity
import com.example.impl.ui.viewmodel.WordsViewModel
import com.example.impl.words_list.WordsListFragment
import com.example.impl.words_list.WordsListViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AddWordFragment: Fragment() {
    private val viewModel: WordsViewModel by viewModels {
        WordsListViewModelFactory(requireContext().applicationContext)
    }
    private val imageLoader by inject<ImageLoader<ImageView>>()
    private val editWordApi by inject<EditWordApi>()
    private val wordInfoService = WordInfoService()
    
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.addwords, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeButton = view.findViewById<ImageView>(R.id.close)
        val container = view.findViewById<ViewGroup>(R.id.addwords)
        val wordInput = view.findViewById<EditText>(R.id.wordInput)
        val foundText = view.findViewById<TextView>(R.id.found_text)
        val instructionText = view.findViewById<TextView>(R.id.instruction_text)

        closeButton?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        wordInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                val query = s?.toString()?.trim()
                if (query.isNullOrEmpty()) {
                    container.removeAllViews()
                    foundText?.visibility = View.GONE
                    instructionText?.visibility = View.VISIBLE
                    return
                }
                
                instructionText?.visibility = View.GONE
                foundText?.visibility = View.VISIBLE
                searchJob = lifecycleScope.launch {
                    delay(500)
                    if (query.isNotEmpty()) {
                        searchWord(query, container)
                    }
                }
            }
        })
    }
    
    private suspend fun searchWord(query: String, container: ViewGroup) {
        container.removeAllViews()
        
        val result = wordInfoService.getWordInfo(query)
        result.fold(
            onSuccess = { wordInfo ->
                view?.post {
                    showWordCard(container, wordInfo)
                }
            },
            onFailure = { error ->
                Log.e("AddWordFragment", "Error fetching word info", error)
                view?.post {
                    container.removeAllViews()
                }
            }
        )
    }
    
    private fun showWordCard(container: ViewGroup, wordInfo: WordInfoResponse) {
        container.removeAllViews()
        
        val card = layoutInflater.inflate(R.layout.addword, container, false)
        val title = card.findViewById<TextView>(R.id.addword_title)
        val translation = card.findViewById<TextView>(R.id.addword_translation)
        val pronunciation = card.findViewById<TextView>(R.id.addword_pronunciation)
        val image = card.findViewById<ImageView>(R.id.addword_image)
        val addButton = card.findViewById<Button>(R.id.addword_addbutton)
        container.addView(card)

        title.text = wordInfo.title
        translation.text = wordInfo.translation
        pronunciation.text = wordInfo.pronunciation
        imageLoader.load(image, wordInfo.img)

        card.setOnClickListener {
            val editFragment = editWordApi.getEditWordFragmentForAdd(
                title = wordInfo.title,
                translation = wordInfo.translation,
                pronunciation = wordInfo.pronunciation,
                img = wordInfo.img
            )
            val containerId = requireActivity().resources.getIdentifier("main", "id", requireActivity().packageName)
            parentFragmentManager.beginTransaction()
                .replace(containerId, editFragment)
                .addToBackStack("add_word")
                .commit()
        }

        addButton.setOnClickListener {
            val wordEntity = WordEntity(
                title = wordInfo.title,
                translation = wordInfo.translation,
                pronunciation = wordInfo.pronunciation,
                img = wordInfo.img
            )
            viewModel.insertWord(wordEntity)
            
            val containerId = requireActivity().resources.getIdentifier("main", "id", requireActivity().packageName)
            parentFragmentManager.beginTransaction()
                .replace(containerId, WordsListFragment())
                .commit()
        }
    }
}

