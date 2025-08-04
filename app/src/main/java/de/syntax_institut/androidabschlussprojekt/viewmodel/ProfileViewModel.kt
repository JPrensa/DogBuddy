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
import android.net.Uri
import de.syntax_institut.androidabschlussprojekt.model.UserProfile
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    val dogs = mutableStateListOf<Dog>()
    val caredDogs = mutableStateListOf<Dog>()

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
    var profileImageUrl by mutableStateOf<String?>(null)
        private set


    init {
        viewModelScope.launch {
            FirestoreRepository.getUserDogsFlow().collect { list ->
                dogs.clear()
                dogs.addAll(list)
            }
        }
        viewModelScope.launch {
            FirestoreRepository.getUserProfileFlow().collect { profile ->
                profileImageUrl = profile.imageUrl
                name = profile.name
                email = profile.email
                phone = profile.phone
                age = profile.age
                address = profile.address
            }
        }
        viewModelScope.launch {
            FirestoreRepository.getCaredDogsFlow().collect { list ->
                caredDogs.clear()
                caredDogs.addAll(list)
            }
        }
    }

    // Edit profile handlers
    fun onNameChange(new: String) { name = new }
    fun onEmailChange(new: String) { email = new }
    fun onPhoneChange(new: String) { phone = new }
    fun onAgeChange(new: String) { age = new }
    fun onAddressChange(new: String) { address = new }

    /** Save updated profile to Firestore **/
    fun saveProfile(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            // Prepare UserProfile with current data
            val profile = UserProfile(
                name = name,
                email = email,
                phone = phone,
                age = age,
                address = address,
                imageUrl = profileImageUrl
            )
            FirestoreRepository.updateUserProfile(profile)
            onDone()
        }
    }

    fun refresh() {
        name = UserRepository.name
        email = UserRepository.email
        phone = UserRepository.phone
        age = UserRepository.age
        address = UserRepository.address
    }

    /** Aktualisiert die gefütterten Mahlzeiten und benachrichtigt den Besitzer **/
    fun updateMeals(dogId: String, meals: Int) {
        viewModelScope.launch {
            try {
                FirestoreRepository.updateMeals(dogId, meals)
            } catch (e: Exception) {
                // TODO: Fehlerbehandlung
            }
        }
    }

    /** Aktualisiert die Spaziergänge und benachrichtigt den Besitzer **/
    fun updateWalks(dogId: String, walks: Int) {
        viewModelScope.launch {
            try {
                FirestoreRepository.updateWalks(dogId, walks)
            } catch (e: Exception) {
                // TODO: Fehlerbehandlung
            }
        }
    }

    /** Upload profile image and update Firebase **/
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                val url = FirestoreRepository.uploadProfileImage(imageUri)
                if (url != null) {
                    profileImageUrl = url
                }
            } catch (e: Exception) {
                // TODO: Fehlerbehandlung
            }
        }
    }

    /** Delete a dog belonging to the user **/
    fun deleteDog(dogId: String) {
        viewModelScope.launch {
            try {
                FirestoreRepository.deleteDog(dogId)
            } catch (e: Exception) {
                // TODO: Fehlerbehandlung
            }
        }
    }
}
