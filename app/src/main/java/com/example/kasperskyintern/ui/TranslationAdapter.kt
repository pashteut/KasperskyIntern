package com.example.kasperskyintern.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.kasperskyintern.model.TranslationItem

class TranslationAdapter(
    private val onFavouritesButtonClick: (TranslationItem) -> Unit,
    private val onDeleteButtonClick: (TranslationItem) -> Unit
) : ListAdapter<TranslationItem, TranslationVH>(TranslationDiffItemCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TranslationVH(TranslationVH.inflateFrom(parent), onFavouritesButtonClick, onDeleteButtonClick)

    override fun onBindViewHolder(holder: TranslationVH, position: Int) {
        holder.bind(getItem(position))
    }
}