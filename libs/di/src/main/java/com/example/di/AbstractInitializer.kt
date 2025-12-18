package com.example.di

import androidx.startup.Initializer

abstract class AbstractInitializer<T>: Initializer<T> {
    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(
            KoinInitializer::class.java
        )
    }
}