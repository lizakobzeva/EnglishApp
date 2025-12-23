package com.example.englishapp.add_word

import androidx.lifecycle.ViewModel

class AddedWordsViewModel: ViewModel() {
    fun addedWords(): List<AddWords> {
        return AddWords.entries
    }
}