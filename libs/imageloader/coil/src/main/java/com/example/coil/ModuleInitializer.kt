package com.example.coil

import android.content.Context
import android.widget.ImageView
import com.example.api.ImageLoader
import com.example.di.AbstractInitializer
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

internal class ModuleInitializer: AbstractInitializer<Unit>() {
    override fun create(context: Context){
        loadKoinModules(
            module{
                single<ImageLoader<ImageView>> { CoilImageLoader() }
            }
        )
    }
}