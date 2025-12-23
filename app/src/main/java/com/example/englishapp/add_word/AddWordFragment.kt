package com.example.englishapp.add_word

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.example.api.ImageLoader
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.englishapp.R

import org.koin.android.ext.android.inject
import kotlin.getValue

class AddWordFragment: Fragment() {
    private val viewModel: AddedWordsViewModel by viewModels()
    private val imageLoader by inject<ImageLoader<ImageView>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.addwords, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<ViewGroup>(R.id.addwords)

//        val addWordButton = view.findViewById<ImageButton>(R.id.btn_add)
//
//        addWordButton.setOnClickListener {
//            val addWordFragment = AddWordFragment()
//
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.main, addWordFragment)
//                .addToBackStack("addwords")
//                .commit()
//
//        }

        viewModel.addedWords().forEach { word ->
            val card = layoutInflater.inflate(R.layout.addword, container, false)
            val title = card.findViewById<TextView>(R.id.addword_title)
            val translation = card.findViewById<TextView>(R.id.addword_translation)
            val pronunciation = card.findViewById<TextView>(R.id.addword_pronunciation)
            val image = card.findViewById<ImageView>(R.id.addword_image)
            container.addView(card)

            title.text = word.title
            translation.text = word.translation
            pronunciation.text = word.pronunciation
            imageLoader.load(image, word.img)

//            card.setOnClickListener {
//                val editWordFragment = EditWordFragment()
//
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.main, editWordFragment)
//                    .addToBackStack("addwords")
//                    .commit()
//            }
        }
    }


}