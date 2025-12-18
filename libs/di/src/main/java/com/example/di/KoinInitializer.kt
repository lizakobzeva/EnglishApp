package com.example.di

import android.content.Context
import androidx.startup.Initializer
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin

class KoinInitializer: Initializer<Koin> {
    override fun create(context: Context): Koin {
        return startKoin{
            androidContext(context.applicationContext)
        }.koin
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }
}