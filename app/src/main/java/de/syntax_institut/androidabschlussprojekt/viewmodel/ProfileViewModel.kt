package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import androidx.compose.runtime.mutableStateListOf
import android.net.Uri
import com.google.firebase.firestore.ListenerRegistration
import de.syntax_institut.androidabschlussprojekt.model.Dog
import de.syntax_institut.androidabschlussprojekt.data.UserRepository

class ProfileViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var listener: ListenerRegistration? = null
    val dogs = mutableStateListOf<Dog>()

    init {
        auth.currentUser?.uid?.let { uid ->
        listener = db.collection("dogs")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                dogs.clear()
                snapshot.documents.forEach { doc ->
                    val id = doc.getString("id") ?: return@forEach
                    val name = doc.getString("name") ?: ""
                    val age = doc.getString("age") ?: ""
                    val breed = doc.getString("breed") ?: ""
                    val description = doc.getString("description")
                    val unavailableFrom = doc.getString("unavailableFrom")
                    val unavailableTo = doc.getString("unavailableTo")
                    val imageUriStr = doc.getString("imageUri")
                    val imageUri = imageUriStr?.let { Uri.parse(it) }
                    dogs.add(Dog(id = id, name = name, age = age, breed = breed, imageUri = imageUri, description = description, unavailableFrom = unavailableFrom, unavailableTo = unavailableTo))
                }
            }
        }
    }
    

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
