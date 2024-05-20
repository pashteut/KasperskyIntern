package com.example.kasperskyintern

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kasperskyintern.databinding.TranslatedItemVhBinding

class TranslationVH(private val binding: TranslatedItemVhBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(translation: TranslationItem) {
        binding.translation = translation
    }
}