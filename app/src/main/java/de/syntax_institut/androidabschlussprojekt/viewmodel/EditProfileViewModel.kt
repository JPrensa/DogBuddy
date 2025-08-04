package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import de.syntax_institut.androidabschlussprojekt.data.UserRepository
import de.syntax_institut.androidabschlussprojekt.model.UserProfile

class EditProfileViewModel : ViewModel() {
    var name by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var phone by mutableStateOf("")
        private set
    var age by mutableStateOf("")
        private set
    var address by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            UserRepository.getUserProfileFlow().collect { profile ->
                name = profile.name
                email = profile.email
                phone = profile.phone
                age = profile.age
                address = profile.address
            }
        }
    }

    fun onNameChange(new: String) { name = new }
    fun onEmailChange(new: String) { email = new }
    fun onPhoneChange(new: String) { phone = new }
    fun onAgeChange(new: String) { age = new }
    fun onAddressChange(new: String) { address = new }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            // Firestore aktualisieren
            UserRepository.updateUserProfile(
                UserProfile(
                    name = name,
                    email = email,
                    phone = phone,
                    age = age,
                    address = address
                )
            )
            // Lokale Repository-Werte setzen
            
            
            
            
            
            onDone()
        }
    }
}
