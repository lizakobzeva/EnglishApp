package com.example.impl.addword

import com.example.api.AddWordApi
import androidx.fragment.app.Fragment

class AddWordApiImpl : AddWordApi {
    override fun getAddWordFragment(): Fragment {
        return AddWordFragment()
    }
}


