package com.example.kasperskyintern

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TranslationItem::class], version = 1)
abstract class TranslationDatabase :RoomDatabase() {
    abstract fun translationDAO(): TranslationDAO

    companion object {
        @Volatile
        private var INSTANCE: TranslationDatabase? = null
        fun getInstance(context: Context): TranslationDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TranslationDatabase::class.java,
                        "translation"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}