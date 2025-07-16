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
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.ktx.auth
import android.util.Log

class UploadViewModel : ViewModel() {
    companion object {
        private const val TAG = "UploadViewModel"
    }
    private val db = Firebase.firestore
    private val auth = Firebase.auth
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
        val userId = auth.currentUser?.uid ?: return
    val id = System.currentTimeMillis().toString()
    val dogData = mapOf(
        "id" to id,
        "userId" to userId,
        "name" to name,
        "age" to age,
        "breed" to breed,
        "description" to description,
        "unavailableFrom" to unavailableFrom,
        "unavailableTo" to unavailableTo,
        "imageUri" to imageUri?.toString()
    )
    db.collection("dogs").document(id)
        .set(dogData)
        .addOnSuccessListener {
            Log.d(TAG, "Successfully added dog with id: $id")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error adding dog", e)
        }
}
}
