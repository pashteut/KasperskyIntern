package com.example.kasperskyintern.model

data class Meaning (
    val id               : Int?         = null,
    val partOfSpeechCode : String?      = null,
    val translation      : Translation? = null,
    val previewUrl       : String?      = null,
    val imageUrl         : String?      = null,
    val transcription    : String?      = null,
    val soundUrl         : String?      = null
)