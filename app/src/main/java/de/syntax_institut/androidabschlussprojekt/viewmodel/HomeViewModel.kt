package de.syntax_institut.androidabschlussprojekt.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObjects
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import de.syntax_institut.androidabschlussprojekt.model.Dog

class HomeViewModel : ViewModel() {
    companion object {
        private const val TAG = "HomeViewModel"
    }
    private val db = Firebase.firestore
    private val _dogs = MutableStateFlow<List<Dog>>(emptyList())
    val dogs: StateFlow<List<Dog>> = _dogs.asStateFlow()
    private var listener: ListenerRegistration? = null

    init {
        listener = db.collection("dogs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
    Log.e(TAG, "Firestore listener error", error)
    return@addSnapshotListener
}
if (snapshot == null) {
    Log.w(TAG, "Firestore snapshot is null")
    return@addSnapshotListener
}
Log.d(TAG, "Fetched ${snapshot.documents.size} dogs from Firestore")
                val list = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getString("id") ?: return@mapNotNull null
                    val name = doc.getString("name") ?: return@mapNotNull null
                    val age = doc.getString("age") ?: ""
                    val breed = doc.getString("breed") ?: ""
                    val description = doc.getString("description")
                    val unavailableFrom = doc.getString("unavailableFrom")
                    val unavailableTo = doc.getString("unavailableTo")
                    val imageUriStr = doc.getString("imageUri")
                    val imageUri = imageUriStr?.let { Uri.parse(it) }
                    Dog(
                        id = id,
                        name = name,
                        age = age,
                        breed = breed,
                        imageUri = imageUri,
                        description = description,
                        unavailableFrom = unavailableFrom,
                        unavailableTo = unavailableTo
                    )
                }
                _dogs.value = list
            }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}
