package com.example.kasperskyintern.presentator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasperskyintern.database.TranslationDAO
import com.example.kasperskyintern.model.TranslationItem
import kotlinx.coroutines.launch

class FavouritesManagerImpl(private val dao: TranslationDAO) : FavouritesManager, ViewModel() {
    override fun addToFavouritesButtonClicked(translationItem: TranslationItem) {
        viewModelScope.launch {
            dao.update(translationItem.copy(isFavourite = !translationItem.isFavourite))
        }
    }

    override fun deleteButtonClicked(translationItem: TranslationItem) {
        viewModelScope.launch {
            dao.delete(translationItem)
        }
    }
}