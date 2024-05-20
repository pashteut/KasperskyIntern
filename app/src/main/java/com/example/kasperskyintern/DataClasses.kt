package com.example.kasperskyintern

data class Response (
    val id       : Int?                = null,
    val text     : String?             = null,
    val meanings : ArrayList<Meanings> = arrayListOf()
)

data class Meanings (

    val id               : Int?         = null,
    val partOfSpeechCode : String?      = null,
    val translation      : Translation? = null,
    val previewUrl       : String?      = null,
    val imageUrl         : String?      = null,
    val transcription    : String?      = null,
    val soundUrl         : String?      = null

)

data class Translation (
    val text : String? = null,
    val note : String? = null
)