package com.example.kasperskyintern

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val client: HttpClient,
    private val dao: TranslationDAO
) : ViewModel() {
    val text = MutableStateFlow("")
    val translationHistory = dao.getAll()
    private val _translation = MutableStateFlow("")
    val translation: StateFlow<String> = _translation

    init {
        viewModelScope.launch {
            text.collect {
                if (it != "")
                    fetchTranslation(it)
                else
                    _translation.value = ""
            }
        }
    }

    fun textChanged() {
        if (text.value != "") {
            if (_translation.value != "Перевод не найден")
                CoroutineScope(Dispatchers.IO).launch {
                    if (text.value != dao.getLastInserted()?.text)
                        dao.insert(TranslationItem(text = text.value, translation = _translation.value))
                }
        } else
            _translation.value = ""
    }

    private fun fetchTranslation(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
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
}