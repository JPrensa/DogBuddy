package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import de.syntax_institut.androidabschlussprojekt.model.Dog
import de.syntax_institut.androidabschlussprojekt.data.DogRepository

class HomeViewModel : ViewModel() {
    
    
    private val _dogs = MutableStateFlow<List<Dog>>(emptyList()) // now filled from repository
    val dogs: StateFlow<List<Dog>> = _dogs.asStateFlow()
    

    init {
        viewModelScope.launch {
            DogRepository.getAllDogs().collect { list ->
                _dogs.value = list
            }
        }
    
            
    }

    
}
