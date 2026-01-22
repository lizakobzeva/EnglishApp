package com.example.impl.wordsstudy

import com.example.api.WordsStudyApi
import androidx.fragment.app.Fragment

class WordsStudyApiImpl : WordsStudyApi {
    override fun getWordsStudyFragment(): Fragment {
        return WordsStudyFragment()
    }
}


