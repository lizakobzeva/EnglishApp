package com.example.impl.editword

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.api.ImageLoader
import com.example.impl.editword.R
import com.example.impl.data.entity.WordEntity
import com.example.impl.ui.viewmodel.WordsViewModel
import com.example.impl.words_list.WordsListViewModelFactory
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class EditWordFragment: Fragment() {
    private val imageLoader by inject<ImageLoader<ImageView>>()
    private val viewModel: WordsViewModel by viewModels {
        WordsListViewModelFactory(requireContext().applicationContext)
    }
    
    private var currentWordId: Long = -1
    private var isAddMode: Boolean = false
    private var initialTitle: String = ""
    private var initialTranslation: String = ""
    private var initialPronunciation: String = ""
    private var initialImg: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.editword, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val wordIdString = arguments?.getString("name") ?: ""
        currentWordId = wordIdString.toLongOrNull() ?: -1
        
        isAddMode = arguments?.getBoolean("is_add_mode", false) ?: false
        initialTitle = arguments?.getString("title") ?: ""
        initialTranslation = arguments?.getString("translation") ?: ""
        initialPronunciation = arguments?.getString("pronunciation") ?: ""
        initialImg = arguments?.getString("img") ?: ""
        
        Log.d("TECH", "wordId: $currentWordId, isAddMode: $isAddMode")

        val close = view.findViewById<ImageView>(R.id.close)
        val deleteButton = view.findViewById<ImageView>(R.id.delete_word)
        val titleText = view.findViewById<TextView>(R.id.title_text)
        val editWord = view.findViewById<EditText>(R.id.editWord)
        val editTranslation = view.findViewById<EditText>(R.id.editTranslation)
        val editPronunciation = view.findViewById<EditText>(R.id.editPronunciation)
        val editImage = view.findViewById<ImageView>(R.id.edit_word_image)
        val saveButton = view.findViewById<Button>(R.id.btn_learning)

        if (isAddMode) {
            deleteButton.visibility = View.GONE
            titleText?.text = "Добавить слово"
            saveButton.text = "Добавить"
            
            editWord.setText(initialTitle)
            editTranslation.setText(initialTranslation)
            editPronunciation.setText(initialPronunciation)
            if (initialImg.isNotEmpty()) {
                imageLoader.load(editImage, initialImg)
            }
        } else {
            deleteButton.visibility = View.VISIBLE
            titleText?.text = "Редактирование"
            saveButton.text = "Сохранить"
            
            if (currentWordId != -1L) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val word = viewModel.getWordById(currentWordId)
                    word?.let {
                        editWord.setText(it.title)
                        editTranslation.setText(it.translation)
                        editPronunciation.setText(it.pronunciation)
                        imageLoader.load(editImage, it.img)
                    }
                }
            }
        }

        close.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        deleteButton.setOnClickListener {
            if (!isAddMode) {
                showDeleteConfirmationDialog()
            }
        }

        saveButton.setOnClickListener {
            val title = editWord.text.toString().trim()
            val translation = editTranslation.text.toString().trim()
            val pronunciation = editPronunciation.text.toString().trim()
            
            if (title.isEmpty() || translation.isEmpty()) {
                return@setOnClickListener
            }
            
            viewLifecycleOwner.lifecycleScope.launch {
                if (isAddMode) {
                    val newWord = WordEntity(
                        title = title,
                        translation = translation,
                        pronunciation = pronunciation,
                        img = initialImg
                    )
                    viewModel.insertWord(newWord)
                } else {
                    if (currentWordId != -1L) {
                        val word = viewModel.getWordById(currentWordId)
                        word?.let { currentWord ->
                            val updatedWord = currentWord.copy(
                                title = title,
                                translation = translation,
                                pronunciation = pronunciation
                            )
                            viewModel.updateWord(updatedWord)
                        }
                    }
                }
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление слова")
            .setMessage("Вы уверены, что хотите удалить это слово?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteWord()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deleteWord() {
        if (currentWordId != -1L) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.deleteWordById(currentWordId)
                parentFragmentManager.popBackStack()
            }
        }
    }

    companion object {
        fun newInstance(name: String): Fragment {
            val extras = Bundle().apply {
                putString("name", name)
            }

            return EditWordFragment().apply {
                arguments = extras
            }
        }
        
        fun newInstanceForAdd(title: String, translation: String, pronunciation: String, img: String): Fragment {
            val extras = Bundle().apply {
                putBoolean("is_add_mode", true)
                putString("title", title)
                putString("translation", translation)
                putString("pronunciation", pronunciation)
                putString("img", img)
            }

            return EditWordFragment().apply {
                arguments = extras
            }
        }
    }
}

