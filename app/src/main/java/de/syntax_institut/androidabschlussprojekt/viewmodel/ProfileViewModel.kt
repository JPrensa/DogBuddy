package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.syntax_institut.androidabschlussprojekt.data.DogRepository
import de.syntax_institut.androidabschlussprojekt.data.UserRepository

class ProfileViewModel : ViewModel() {
    val dogs = DogRepository.dogs

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

    fun refresh() {
        name = UserRepository.name
        email = UserRepository.email
        phone = UserRepository.phone
        age = UserRepository.age
        address = UserRepository.address
    }
}
