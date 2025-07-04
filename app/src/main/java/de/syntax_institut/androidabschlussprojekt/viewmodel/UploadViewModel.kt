package de.syntax_institut.androidabschlussprojekt.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import de.syntax_institut.androidabschlussprojekt.data.DogRepository
import de.syntax_institut.androidabschlussprojekt.model.Dog

class UploadViewModel : ViewModel() {
    var name by mutableStateOf("")
        private set
    var age by mutableStateOf("")
        private set
    var breed by mutableStateOf("")
        private set
    var imageUri by mutableStateOf<Uri?>(null)
        private set
    var description by mutableStateOf("")
        private set
    var unavailableFrom by mutableStateOf("")
        private set
    var unavailableTo by mutableStateOf("")
        private set

    fun onNameChange(newName: String) { name = newName }
    fun onAgeChange(newAge: String) { age = newAge }
    fun onBreedChange(newBreed: String) { breed = newBreed }
    fun onImageUriChange(uri: Uri?) { imageUri = uri }
    fun onDescriptionChange(new: String) { description = new }
    fun onUnavailableFromChange(new: String) { unavailableFrom = new }
    fun onUnavailableToChange(new: String) { unavailableTo = new }

    fun addDog(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val id = System.currentTimeMillis().toString()
            DogRepository.addDog(Dog(
                id = id,
                name = name,
                age = age,
                breed = breed,
                imageUri = imageUri,
                description = description,
                unavailableFrom = unavailableFrom,
                unavailableTo = unavailableTo
            ))
            onSuccess()
        }
    }
}
