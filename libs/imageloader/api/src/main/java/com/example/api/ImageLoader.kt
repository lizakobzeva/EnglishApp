package com.example.api

interface ImageLoader<T> {
    fun load(target: T, model: Any?)
}