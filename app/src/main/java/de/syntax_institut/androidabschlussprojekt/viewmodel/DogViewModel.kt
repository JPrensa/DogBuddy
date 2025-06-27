package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import de.syntax_institut.androidabschlussprojekt.data.api.DogApi

class DogViewModel : ViewModel() {
    var imageUrl by mutableStateOf<String?>(null)
        private set

    init {
        loadRandom()
    }

    fun loadRandom() {
        viewModelScope.launch {
            runCatching {
                DogApi.service.getRandomDogImage()
            }.onSuccess {
                imageUrl = it.message
            }.onFailure {
                // Fehler beim Laden: hier ggf. Logging oder Fehlerstatus setzen
            }
        }
    }
}
