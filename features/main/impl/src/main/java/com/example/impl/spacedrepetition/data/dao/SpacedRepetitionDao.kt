package com.example.impl.spacedrepetition.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.impl.spacedrepetition.data.entity.SpacedRepetitionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpacedRepetitionDao {
    @Query("SELECT * FROM spaced_repetition WHERE wordId = :wordId")
    suspend fun getByWordId(wordId: Long): SpacedRepetitionEntity?
    
    @Query("SELECT * FROM spaced_repetition WHERE wordId = :wordId")
    fun getByWordIdFlow(wordId: Long): Flow<SpacedRepetitionEntity?>
    
    @Query("SELECT * FROM spaced_repetition")
    fun getAll(): Flow<List<SpacedRepetitionEntity>>
    
    @Query("SELECT * FROM spaced_repetition WHERE wordId IN (:wordIds)")
    suspend fun getByWordIds(wordIds: List<Long>): List<SpacedRepetitionEntity>
    
    @Query("SELECT * FROM spaced_repetition WHERE currentStep = 0")
    fun getNewWords(): Flow<List<SpacedRepetitionEntity>>
    
    @Query("SELECT * FROM spaced_repetition WHERE currentStep > 0 AND currentStep < 6")
    fun getLearningWords(): Flow<List<SpacedRepetitionEntity>>
    
    @Query("SELECT * FROM spaced_repetition WHERE currentStep = 6")
    fun getLearnedWords(): Flow<List<SpacedRepetitionEntity>>
    
    // Получаем только слова, готовые к повторению сегодня (nextReviewDate <= конец сегодняшнего дня)
    @Query("SELECT * FROM spaced_repetition WHERE (currentStep = 0 OR (currentStep > 0 AND currentStep < 6 AND nextReviewDate <= :endOfToday))")
    suspend fun getWordsForReview(endOfToday: Long): List<SpacedRepetitionEntity>
    
    @Query("SELECT COUNT(*) FROM spaced_repetition WHERE (currentStep = 0 OR (currentStep > 0 AND currentStep < 6 AND nextReviewDate <= :endOfToday))")
    suspend fun getWordsForReviewCount(endOfToday: Long): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SpacedRepetitionEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SpacedRepetitionEntity>)
    
    @Update
    suspend fun update(entity: SpacedRepetitionEntity)
    
    @Query("DELETE FROM spaced_repetition WHERE wordId = :wordId")
    suspend fun deleteByWordId(wordId: Long)
    
    @Query("DELETE FROM spaced_repetition WHERE wordId IN (:wordIds)")
    suspend fun deleteByWordIds(wordIds: List<Long>)
}

