package com.example.kasperskyintern.database

import androidx.room.*
import com.example.kasperskyintern.model.Translation
import com.example.kasperskyintern.model.TranslationItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDAO {
    @Query("SELECT * FROM translation")
    fun getAll() : Flow<List<TranslationItem>>

    @Query("SELECT * FROM translation ORDER BY id DESC")
    fun getAllReversed() : Flow<List<TranslationItem>>

    @Query("SELECT * FROM translation WHERE isFavourite = 1")
    fun getAllFavourites() : Flow<List<TranslationItem>>

    @Query("SELECT * FROM translation WHERE isFavourite = 1 ORDER BY id DESC")
    fun getAllFavouritesReversed() : Flow<List<TranslationItem>>

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