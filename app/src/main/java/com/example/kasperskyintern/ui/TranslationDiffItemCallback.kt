package com.example.kasperskyintern.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.kasperskyintern.model.TranslationItem

class TranslationDiffItemCallback : DiffUtil.ItemCallback<TranslationItem>() {
    override fun areItemsTheSame(oldItem: TranslationItem, newItem: TranslationItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: TranslationItem, newItem: TranslationItem) =
        oldItem == newItem
}