package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import de.syntax_institut.androidabschlussprojekt.data.api.DogDetailsApi
import de.syntax_institut.androidabschlussprojekt.data.api.Breed

class DogDetailsViewModel : ViewModel() {
    var breeds by mutableStateOf<List<Breed>>(emptyList())
        private set

    init {
        loadBreeds()
    }

    fun loadBreeds() {
        viewModelScope.launch {
            runCatching {
                DogDetailsApi.service.getBreeds()
            }.onSuccess {
                breeds = it
            }.onFailure {
                // Fehler beim Laden der Rassen
            }
        }
    }
}
