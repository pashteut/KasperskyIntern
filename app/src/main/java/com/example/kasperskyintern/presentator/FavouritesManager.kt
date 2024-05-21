package com.example.kasperskyintern.presentator

import com.example.kasperskyintern.model.TranslationItem

interface FavouritesManager {
    fun addToFavouritesButtonClicked(translationItem: TranslationItem)
    fun deleteButtonClicked(translationItem: TranslationItem)
}