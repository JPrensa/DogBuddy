package de.syntax_institut.androidabschlussprojekt.data

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import de.syntax_institut.androidabschlussprojekt.model.Dog

object FirestoreRepository {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun getAllDogsFlow(): Flow<List<Dog>> = callbackFlow {
        val listener = db.collection("dogs")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getString("id") ?: return@mapNotNull null
                    val name = doc.getString("name") ?: ""
                    val age = doc.getString("age") ?: ""
                    val breed = doc.getString("breed") ?: ""
                    val description = doc.getString("description")
                    val unavailableFrom = doc.getString("unavailableFrom")
                    val unavailableTo = doc.getString("unavailableTo")
                    val imageUriStr = doc.getString("imageUri")
                    val imageUri = imageUriStr?.let { Uri.parse(it) }
                    Dog(id = id, name = name, age = age, breed = breed, imageUri = imageUri, description = description, unavailableFrom = unavailableFrom, unavailableTo = unavailableTo)
                }
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getUserDogsFlow(): Flow<List<Dog>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val listener = db.collection("dogs")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val list = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getString("id") ?: return@mapNotNull null
                    val name = doc.getString("name") ?: ""
                    val age = doc.getString("age") ?: ""
                    val breed = doc.getString("breed") ?: ""
                    val description = doc.getString("description")
                    val unavailableFrom = doc.getString("unavailableFrom")
                    val unavailableTo = doc.getString("unavailableTo")
                    val imageUriStr = doc.getString("imageUri")
                    val imageUri = imageUriStr?.let { Uri.parse(it) }
                    Dog(id = id, name = name, age = age, breed = breed, imageUri = imageUri, description = description, unavailableFrom = unavailableFrom, unavailableTo = unavailableTo)
                }
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addDog(dog: Dog) {
        val uid = auth.currentUser?.uid ?: return
        val data = mapOf(
            "id" to dog.id,
            "userId" to uid,
            "name" to dog.name,
            "age" to dog.age,
            "breed" to dog.breed,
            "description" to dog.description,
            "unavailableFrom" to dog.unavailableFrom,
            "unavailableTo" to dog.unavailableTo,
            "imageUri" to dog.imageUri?.toString(),
            "interestedUsers" to listOf<String>()
        )
        db.collection("dogs").document(dog.id).set(data).await()
    }

    suspend fun requestCare(dogId: String): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        db.collection("dogs").document(dogId)
            .update("interestedUsers", FieldValue.arrayUnion(uid)).await()
        return true
    }
}
