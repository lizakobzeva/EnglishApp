package com.example.englishapp.edit_word

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.api.ImageLoader
import com.example.englishapp.R
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
        
        Log.d("TECH", "wordId: $currentWordId")

        val close = view.findViewById<ImageView>(R.id.close)
        val deleteButton = view.findViewById<ImageView>(R.id.delete_word)
        val editWord = view.findViewById<EditText>(R.id.editWord)
        val editTranslation = view.findViewById<EditText>(R.id.editTranslation)
        val editPronunciation = view.findViewById<EditText>(R.id.editPronunciation)
        val editImage = view.findViewById<ImageView>(R.id.edit_word_image)
        val saveButton = view.findViewById<Button>(R.id.btn_learning)

        close.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Обработчик удаления слова
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Загружаем данные слова из базы данных
        if (currentWordId != -1L) {
            viewLifecycleOwner.lifecycleScope.launch {
                val word = viewModel.getWordById(currentWordId)
                word?.let {
                    // Заполняем поля данными слова
                    editWord.setText(it.title)
                    editTranslation.setText(it.translation)
                    editPronunciation.setText(it.pronunciation)
                    imageLoader.load(editImage, it.img)
                }
            }
        }

        // Сохранение изменений
        saveButton.setOnClickListener {
            if (currentWordId != -1L) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val word = viewModel.getWordById(currentWordId)
                    word?.let { currentWord ->
                        val updatedWord = currentWord.copy(
                            title = editWord.text.toString().trim(),
                            translation = editTranslation.text.toString().trim(),
                            pronunciation = editPronunciation.text.toString().trim()
                        )
                        viewModel.updateWord(updatedWord)
                        parentFragmentManager.popBackStack()
                    }
                }
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
    }
}