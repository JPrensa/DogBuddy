package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import de.syntax_institut.androidabschlussprojekt.data.FirestoreRepository
import de.syntax_institut.androidabschlussprojekt.data.UserRepository
import de.syntax_institut.androidabschlussprojekt.model.Dog

class ProfileViewModel : ViewModel() {

    val dogs = mutableStateListOf<Dog>()

    var name by mutableStateOf(UserRepository.name)
        private set
    var email by mutableStateOf(UserRepository.email)
        private set
    var phone by mutableStateOf(UserRepository.phone)
        private set
    var age by mutableStateOf(UserRepository.age)
        private set
    var address by mutableStateOf(UserRepository.address)
        private set

    init {
        viewModelScope.launch {
            FirestoreRepository.getUserDogsFlow().collect { list ->
                dogs.clear()
                dogs.addAll(list)
            }
        }
    }

    fun refresh() {
        name = UserRepository.name
        email = UserRepository.email
        phone = UserRepository.phone
        age = UserRepository.age
        address = UserRepository.address
    }
}
