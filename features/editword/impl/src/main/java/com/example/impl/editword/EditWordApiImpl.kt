package com.example.impl.editword

import com.example.api.EditWordApi
import androidx.fragment.app.Fragment

class EditWordApiImpl : EditWordApi {
    override fun getEditWordFragment(wordId: String): Fragment {
        return EditWordFragment.newInstance(wordId)
    }

    override fun getEditWordFragmentForAdd(title: String, translation: String, pronunciation: String, img: String): Fragment {
        return EditWordFragment.newInstanceForAdd(title, translation, pronunciation, img)
    }
}

