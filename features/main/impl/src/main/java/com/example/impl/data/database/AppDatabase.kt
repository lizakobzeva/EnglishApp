package com.example.impl.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.impl.data.dao.WordDao
import com.example.impl.data.entity.WordEntity
import com.example.impl.spacedrepetition.data.dao.SpacedRepetitionDao
import com.example.impl.spacedrepetition.data.entity.SpacedRepetitionEntity

@Database(
    entities = [WordEntity::class, SpacedRepetitionEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun spacedRepetitionDao(): SpacedRepetitionDao
    
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Создаем таблицу spaced_repetition
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS spaced_repetition (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        wordId INTEGER NOT NULL,
                        currentStep INTEGER NOT NULL DEFAULT 0,
                        nextReviewDate INTEGER NOT NULL DEFAULT 0,
                        interval INTEGER NOT NULL DEFAULT 0,
                        easeFactor REAL NOT NULL DEFAULT 2.5,
                        lastReviewDate INTEGER NOT NULL DEFAULT 0,
                        consecutiveCorrectAnswers INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(wordId) REFERENCES words(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                // Создаем индекс для wordId
                database.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_spaced_repetition_wordId 
                    ON spaced_repetition(wordId)
                """.trimIndent())
            }
        }
    }
}


