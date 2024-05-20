package com.example.kasperskyintern

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kasperskyintern.databinding.TranslatedItemVhBinding

class TranslationAdapter() : RecyclerView.Adapter<TranslationVH>() {

    var translations = mutableListOf<TranslationItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TranslationVH(
            TranslatedItemVhBinding
                .inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        )

    override fun getItemCount() = translations.size

    override fun onBindViewHolder(holder: TranslationVH, position: Int) {
        holder.bind(translations[position])
    }
}