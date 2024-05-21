package com.example.kasperskyintern.model

data class Response (
    val id       : Int?                = null,
    val text     : String?             = null,
    val meanings : ArrayList<Meaning> = arrayListOf()
)