package com.example.kasperskyintern.presentator

import androidx.lifecycle.ViewModel
import com.example.kasperskyintern.database.TranslationDAO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavouritesFragmentViewModel @Inject constructor(private val dao: TranslationDAO)
    : ViewModel(), FavouritesManager by FavouritesManagerImpl(dao) {
    val favouriteTranslationHistory = dao.getAllFavouritesReversed()
}