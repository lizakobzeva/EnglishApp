package com.example.impl.data.repository

import com.example.impl.data.dao.WordDao
import com.example.impl.data.entity.WordEntity
import kotlinx.coroutines.flow.Flow

class WordsRepository(private val wordDao: WordDao) {
    fun getAllWords(): Flow<List<WordEntity>> {
        return wordDao.getAllWords()
    }
    
    suspend fun getWordById(id: Long): WordEntity? {
        return wordDao.getWordById(id)
    }
    
    suspend fun insertWord(word: WordEntity): Long {
        return wordDao.insert(word)
    }
    
    suspend fun insertWords(words: List<WordEntity>) {
        wordDao.insertAll(words)
    }
    
    suspend fun updateWord(word: WordEntity) {
        wordDao.update(word)
    }
    
    suspend fun deleteWord(word: WordEntity) {
        wordDao.delete(word)
    }
    
    suspend fun deleteWordById(id: Long) {
        wordDao.deleteById(id)
    }
    
    suspend fun getWordCount(): Int {
        return wordDao.getWordCount()
    }
}


