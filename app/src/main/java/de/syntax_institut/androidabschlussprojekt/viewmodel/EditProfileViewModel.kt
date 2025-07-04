package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.syntax_institut.androidabschlussprojekt.data.UserRepository

class EditProfileViewModel : ViewModel() {
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

    fun onNameChange(new: String) { name = new }
    fun onEmailChange(new: String) { email = new }
    fun onPhoneChange(new: String) { phone = new }
    fun onAgeChange(new: String) { age = new }
    fun onAddressChange(new: String) { address = new }

    fun save(onDone: () -> Unit) {
        UserRepository.name = name
        UserRepository.email = email
        UserRepository.phone = phone
        UserRepository.age = age
        UserRepository.address = address
        onDone()
    }
}
