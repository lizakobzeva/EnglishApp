package com.example.coil

import android.widget.ImageView
import coil3.load
import com.example.api.ImageLoader

internal class CoilImageLoader : ImageLoader<ImageView> {
    override fun load(target: ImageView, model: Any?) {
        target.load(model) {
            listener(
                onError = {_, error -> error.throwable.printStackTrace()}
            )
        }
    }
}