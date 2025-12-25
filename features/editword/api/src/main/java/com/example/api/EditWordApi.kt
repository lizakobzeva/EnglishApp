package com.example.api

import androidx.fragment.app.Fragment

interface EditWordApi {
    fun getEditWordFragment(wordId: String): Fragment
    fun getEditWordFragmentForAdd(title: String, translation: String, pronunciation: String, img: String): Fragment
}

