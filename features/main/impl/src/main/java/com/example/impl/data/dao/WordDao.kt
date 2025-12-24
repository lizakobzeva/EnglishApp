package com.example.impl.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.impl.data.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY id ASC")
    fun getAllWords(): Flow<List<WordEntity>>
    
    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): WordEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: WordEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)
    
    @Update
    suspend fun update(word: WordEntity)
    
    @Delete
    suspend fun delete(word: WordEntity)
    
    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int
}

