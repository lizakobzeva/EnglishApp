package com.example.impl.data.database

import android.util.Log
import com.example.impl.data.dao.WordDao
import com.example.impl.data.entity.WordEntity
import com.example.impl.spacedrepetition.data.dao.SpacedRepetitionDao
import com.example.impl.spacedrepetition.data.entity.SpacedRepetitionEntity
import kotlinx.coroutines.flow.first

object DatabaseInitializer {
    suspend fun initialize(database: AppDatabase) {
        val wordDao = database.wordDao()
        val spacedRepetitionDao = database.spacedRepetitionDao()
        
        val allRequiredWords = listOf(
                WordEntity(
                    title = "Hello",
                    translation = "Привет",
                    pronunciation = "[kaet]",
                    img = "https://www.shutterstock.com/shutterstock/photos/1990073147/display_1500/stock-vector-hi-hello-woman-waving-hand-welcome-hand-drawn-style-vector-design-illustrations-1990073147.jpg"
                ),
                WordEntity(
                    title = "Vivid",
                    translation = "Яркий",
                    pronunciation = "[kaet]",
                    img = "https://www.shutterstock.com/shutterstock/photos/2649673407/display_1500/stock-photo-colorful-vivid-abstract-pattern-oil-color-flowing-full-frame-2649673407.jpg"
                ),
                WordEntity(
                    title = "Curse",
                    translation = "Проклятие",
                    pronunciation = "[kaet]",
                    img = "https://www.shutterstock.com/shutterstock/photos/2507720605/display_1500/stock-vector-woman-with-needle-and-voodoo-doll-practices-black-magic-and-casts-curse-on-victim-sitting-among-2507720605.jpg"
                ),
                WordEntity(
                    title = "Water",
                    translation = "Вода",
                    pronunciation = "[kaet]",
                    img = "https://www.shutterstock.com/shutterstock/photos/2514687013/display_1500/stock-photo-defocus-blurred-transparent-blue-colored-clear-calm-water-surface-texture-with-splash-and-bubble-2514687013.jpg"
                ),
                WordEntity(
                    title = "Computer",
                    translation = "Компьютер",
                    pronunciation = "[kaet]",
                    img = "https://www.shutterstock.com/shutterstock/photos/2498526443/display_1500/stock-photo-close-up-woman-hand-typing-on-laptop-computer-keyboard-business-woman-online-working-on-laptop-2498526443.jpg"
                ),
                WordEntity(
                    title = "Cat",
                    translation = "Кот",
                    pronunciation = "[kaet]",
                    img = "https://www.shutterstock.com/shutterstock/photos/2545741079/display_1500/stock-photo-cat-on-white-background-pet-2545741079.jpg"
                ),
                WordEntity(
                    title = "Face",
                    translation = "Лицо",
                    pronunciation = "[kaet]",
                    img = "https://www.shutterstock.com/shutterstock/photos/2412268863/display_1500/stock-photo-woman-portrait-and-skincare-glow-or-smiling-studio-and-happy-with-facial-treatment-by-white-2412268863.jpg"
                ),
                WordEntity(
                    title = "Consiquences",
                    translation = "Последствия",
                    pronunciation = "[kaet]",
                    img = "https://www.shutterstock.com/shutterstock/photos/2658836849/display_1500/stock-vector-consequence-icon-element-for-design-2658836849.jpg"
                ),
                WordEntity(
                    title = "Frog",
                    translation = "Лягушка",
                    pronunciation = "[frɔːɡ]",
                    img = "https://www.shutterstock.com/ru/image-photo/red-eyed-tree-frog-hanging-on-2520897919?trackingId=390d1b7b-2527-49e1-87cf-5dbe5e86123b"
                ),
                WordEntity(
                    title = "Agile",
                    translation = "Ловкий",
                    pronunciation = "[ˈædʒaɪl]",
                    img = "https://www.shutterstock.com/ru/image-photo/full-body-agile-young-male-using-2023786472?trackingId=1a15fd0b-a75f-4aa3-96a1-3f85ea70a449"
                ),
                WordEntity(
                    title = "Fox",
                    translation = "Лиса",
                    pronunciation = "[foks]",
                    img = "https://www.shutterstock.com/ru/image-photo/wild-red-fox-vulpes-scavenging-foraging-2540341701?trackingId=0b4a76f3-5707-4652-a043-593cc8463001"
                )

        )
        
        val currentCount = wordDao.getWordCount()
        
        if (currentCount == 0) {
            wordDao.insertAll(allRequiredWords)
        } else {
            try {
                val existingWords = wordDao.getAllWords().first()
                val existingTitles = existingWords.map { it.title }.toSet()
                
                val wordsToAdd = allRequiredWords.filter { word ->
                    !existingTitles.contains(word.title)
                }
                if (wordsToAdd.isNotEmpty()) {
                    wordDao.insertAll(wordsToAdd)
                }
            } catch (e: Exception) {
            }
        }
        
        // Инициализируем интервальное повторение для всех существующих слов
        try {
            val allWords = wordDao.getAllWords().first()
            Log.d("DatabaseInitializer", "Total words found: ${allWords.size}")
            
            val existingSpacedRepetitions = spacedRepetitionDao.getByWordIds(allWords.map { it.id })
            val existingWordIds = existingSpacedRepetitions.map { it.wordId }.toSet()
            Log.d("DatabaseInitializer", "Existing spaced repetitions: ${existingSpacedRepetitions.size}")
            
            val newSpacedRepetitions = allWords
                .filter { it.id !in existingWordIds }
                .map { word ->
                    SpacedRepetitionEntity(
                        wordId = word.id,
                        currentStep = 0,
                        nextReviewDate = 0,
                        interval = 0,
                        easeFactor = 2.5,
                        lastReviewDate = 0,
                        consecutiveCorrectAnswers = 0
                    )
                }
            
            Log.d("DatabaseInitializer", "New spaced repetitions to create: ${newSpacedRepetitions.size}")
            
            if (newSpacedRepetitions.isNotEmpty()) {
                spacedRepetitionDao.insertAll(newSpacedRepetitions)
                Log.d("DatabaseInitializer", "Successfully initialized ${newSpacedRepetitions.size} words for spaced repetition")
            }
        } catch (e: Exception) {
            Log.e("DatabaseInitializer", "Error initializing spaced repetition", e)
        }
    }
}

