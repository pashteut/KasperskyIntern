package com.example.kasperskyintern.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kasperskyintern.R
import com.example.kasperskyintern.model.TranslationItem
import com.example.kasperskyintern.databinding.TranslatedItemVhBinding

class TranslationVH(
    private val binding: TranslatedItemVhBinding,
    val onFavouritesButtonClick: (TranslationItem) -> Unit,
    val onDeleteButtonClick: (TranslationItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(translation: TranslationItem) {
        binding.translation = translation
        setFavouriteIcon(translation.isFavourite)
        createContextMenu(translation)

        binding.addToFavouritesButton.setOnClickListener {
            onFavouritesButtonClick(translation)
            setFavouriteIcon(translation.isFavourite)
        }
    }

    private fun setFavouriteIcon(isFavourite: Boolean) {
        binding.addToFavouritesButton.setImageResource(
            if (isFavourite) R.drawable.bookmark
            else R.drawable.bookmark_border
        )
    }

    private fun createContextMenu(translationItem: TranslationItem){
        binding.root.setOnCreateContextMenuListener { menu, _, _ ->
            menu.add(0, 0, 0, "Delete")
                .setOnMenuItemClickListener {
                    onDeleteButtonClick(translationItem)
                    true
                }
        }
    }

    companion object {
        fun inflateFrom(parent: ViewGroup) =
            TranslatedItemVhBinding
                .inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
    }
}