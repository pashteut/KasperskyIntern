package com.example.kasperskyintern

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface TranslationDAO {
    @Query("SELECT * FROM translation")
    fun getAll() : Flow<List<TranslationItem>>

    @Query("SELECT * FROM translation WHERE id = :id")
    suspend fun getById(id: Int) : Translation

    @Query("SELECT * FROM translation ORDER BY id DESC LIMIT 1")
    suspend fun getLastInserted(): TranslationItem?

    @Insert
    suspend fun insert(translation: TranslationItem) : Long

    @Update
    suspend fun update(translation: TranslationItem)

    @Delete
    suspend fun delete(translation: TranslationItem)

    @Query ("DELETE FROM translation")
    suspend fun deleteAll()
}