package com.example.kasperskyintern.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation")
data class TranslationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val translation: String,
    val isFavourite: Boolean = false
)