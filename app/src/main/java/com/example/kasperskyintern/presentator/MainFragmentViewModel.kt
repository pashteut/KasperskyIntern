package com.example.kasperskyintern.presentator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasperskyintern.database.TranslationDAO
import com.example.kasperskyintern.model.Response
import com.example.kasperskyintern.model.TranslationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val client: HttpClient,
    private val dao: TranslationDAO
) : ViewModel(), FavouritesManager by FavouriteManagerImpl(dao) {
    val text = MutableStateFlow("")
    val translationHistory = dao.getAllReversed()
    private val _translation = MutableStateFlow("")
    val translation: StateFlow<String> = _translation
    private var fetchTranslationJob: Job? = null

    init {
        viewModelScope.launch {
            text.collect {
                if (it != "")
                    fetchTranslation(it)
                else {
                    fetchTranslationJob?.cancel()
                    _translation.value = ""
                }
            }
        }
    }

    fun textChanged() {
        if (text.value != "") {
            viewModelScope.launch {
                fetchTranslationJob?.join()
                if (_translation.value == "Перевод не найден")
                    this.cancel()

                val lastInserted = async { dao.getLastInserted() }

                if (text.value != lastInserted.await()?.text)
                    dao.insert(TranslationItem(text = text.value, translation = _translation.value))
            }
        } else
            _translation.value = ""
    }

    private fun fetchTranslation(text: String) {
        fetchTranslationJob?.cancel()

        fetchTranslationJob = viewModelScope.launch {
            val response = async {
                client.get("https://dictionary.skyeng.ru/api/public/v1/words/search?search=$text")
            }

            val responseBody = response.await().body<List<Response?>>()

            _translation.value =
                if (responseBody.isEmpty())
                    "Перевод не найден"
                else
                    responseBody[0]?.meanings?.get(0)?.translation?.text ?: "Перевод не найден"
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }
}