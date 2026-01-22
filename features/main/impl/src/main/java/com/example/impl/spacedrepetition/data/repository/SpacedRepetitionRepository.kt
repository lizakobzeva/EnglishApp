package com.example.impl.spacedrepetition.data.repository

import com.example.impl.spacedrepetition.data.dao.SpacedRepetitionDao
import com.example.impl.spacedrepetition.data.entity.SpacedRepetitionEntity
import kotlinx.coroutines.flow.Flow

class SpacedRepetitionRepository(private val dao: SpacedRepetitionDao) {
    suspend fun getByWordId(wordId: Long): SpacedRepetitionEntity? {
        return dao.getByWordId(wordId)
    }
    
    fun getByWordIdFlow(wordId: Long): Flow<SpacedRepetitionEntity?> {
        return dao.getByWordIdFlow(wordId)
    }
    
    fun getAll(): Flow<List<SpacedRepetitionEntity>> {
        return dao.getAll()
    }
    
    suspend fun getByWordIds(wordIds: List<Long>): List<SpacedRepetitionEntity> {
        return dao.getByWordIds(wordIds)
    }
    
    fun getNewWords(): Flow<List<SpacedRepetitionEntity>> {
        return dao.getNewWords()
    }
    
    fun getLearningWords(): Flow<List<SpacedRepetitionEntity>> {
        return dao.getLearningWords()
    }
    
    fun getLearnedWords(): Flow<List<SpacedRepetitionEntity>> {
        return dao.getLearnedWords()
    }
    
    suspend fun getWordsForReview(endOfToday: Long): List<SpacedRepetitionEntity> {
        return dao.getWordsForReview(endOfToday)
    }
    
    suspend fun getWordsForReviewCount(endOfToday: Long): Int {
        return dao.getWordsForReviewCount(endOfToday)
    }
    
    suspend fun insert(entity: SpacedRepetitionEntity): Long {
        return dao.insert(entity)
    }
    
    suspend fun insertAll(entities: List<SpacedRepetitionEntity>) {
        dao.insertAll(entities)
    }
    
    suspend fun update(entity: SpacedRepetitionEntity) {
        dao.update(entity)
    }
    
    suspend fun deleteByWordId(wordId: Long) {
        dao.deleteByWordId(wordId)
    }
    
    suspend fun deleteByWordIds(wordIds: List<Long>) {
        dao.deleteByWordIds(wordIds)
    }
}

