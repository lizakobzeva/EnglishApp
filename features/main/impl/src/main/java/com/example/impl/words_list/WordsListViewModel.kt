package com.example.impl.words_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.impl.data.database.DatabaseProvider
import com.example.impl.data.repository.WordsRepository
import com.example.impl.ui.viewmodel.WordsViewModel

class WordsListViewModelFactory(private val applicationContext: android.content.Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordsViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(applicationContext)
            val repository = WordsRepository(database.wordDao())
            @Suppress("UNCHECKED_CAST")
            return WordsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}