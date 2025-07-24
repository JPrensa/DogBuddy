package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import de.syntax_institut.androidabschlussprojekt.data.FirestoreRepository

class DogDetailViewModel : ViewModel() {
    private val _requested = MutableStateFlow(false)
    val requested: StateFlow<Boolean> = _requested.asStateFlow()

    fun requestCare(dogId: String) {
        viewModelScope.launch {
            try {
                val success = FirestoreRepository.requestCare(dogId)
                if (success) {
                    _requested.value = true
                }
            } catch (e: Exception) {
                // TODO: handle error (e.g., log)
            }
        }
    }
}
