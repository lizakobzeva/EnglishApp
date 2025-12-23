package com.example.englishapp.com.example.impl.words_list

import androidx.lifecycle.ViewModel

class WordsListViewModel: ViewModel() {
    fun wordsList(): List<Words> {
        return Words.entries
    }
}