package com.example.englishapp.edit_word

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.api.ImageLoader
import com.example.englishapp.R
import org.koin.android.ext.android.inject
import kotlin.getValue


class EditWordFragment: Fragment() {
    private val imageLoader by inject<ImageLoader<ImageView>>()

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


        val close = view.findViewById<ImageView>(R.id.close)

        close.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val image = view.findViewById<ImageView>(R.id.edit_word_image)
        imageLoader.load(image,
            "https://www.shutterstock.com/shutterstock/photos/2587054363/display_1500/stock-photo-ginger-cat-levitates-and-meditates-sits-on-a-rug-in-the-lotus-position-the-cat-s-eyes-are-closed-2587054363.jpg")

    }
}