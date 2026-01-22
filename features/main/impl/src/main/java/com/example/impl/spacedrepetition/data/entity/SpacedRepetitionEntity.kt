package com.example.impl.spacedrepetition.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.impl.data.entity.WordEntity

@Entity(
    tableName = "spaced_repetition",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["wordId"], unique = true)]
)
data class SpacedRepetitionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long,
    val currentStep: Int = 0, // 0 = новое слово, 1-5 = этапы повторения, 6 = выучено
    val nextReviewDate: Long = 0, // timestamp в миллисекундах
    val interval: Int = 0, // интервал в днях
    val easeFactor: Double = 2.5, // коэффициент легкости (SM-2)
    val lastReviewDate: Long = 0, // дата последнего повторения
    val consecutiveCorrectAnswers: Int = 0 // количество правильных ответов подряд
)

