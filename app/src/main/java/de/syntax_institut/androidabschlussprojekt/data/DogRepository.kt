package de.syntax_institut.androidabschlussprojekt.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import android.net.Uri
import android.util.Base64
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import de.syntax_institut.androidabschlussprojekt.model.Dog


object DogRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // Alle Hunde
    fun getAllDogs(): Flow<List<Dog>> = callbackFlow {
        val registration = db.collection("dogs")
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val list = snap.documents.mapNotNull { doc -> docToDog(doc) }
                trySend(list).isSuccess
            }
        awaitClose { registration.remove() }
    }

    // Eigene Hunde
    fun getUserDogs(): Flow<List<Dog>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val registration = db.collection("dogs")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val list = snap.documents.mapNotNull { doc -> docToDog(doc) }
                trySend(list).isSuccess
            }
        awaitClose { registration.remove() }
    }

    // Hunde, die ich betreue
    fun getCaredDogs(): Flow<List<Dog>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val registration = db.collection("dogs")
            .whereArrayContains("interestedUsers", uid)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val list = snap.documents.mapNotNull { doc ->
                    docToDog(doc)?.copy(
                        isCared = true,
                        mealsGiven = (doc.getLong("mealsGiven") ?: 0).toInt(),
                        walksDone = (doc.getLong("walksDone") ?: 0).toInt(),
                        totalWalks = (doc.getLong("totalWalks") ?: 0).toInt()
                    )
                }
                trySend(list).isSuccess
            }
        awaitClose { registration.remove() }
    }

    // IDs interessierter Nutzer
    fun getInterestedUsers(dogId: String): Flow<List<String>> = callbackFlow {
        val registration = db.collection("dogs").document(dogId)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null || !snap.exists()) return@addSnapshotListener
                val uids = snap.get("interestedUsers") as? List<String> ?: emptyList()
                trySend(uids).isSuccess
            }
        awaitClose { registration.remove() }
    }

    // Anfrage zum Aufpassen
    suspend fun requestCare(dogId: String): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        db.collection("dogs").document(dogId)
            .update("interestedUsers", FieldValue.arrayUnion(uid))
            .await()
        return true
    }

    // Neuen Hund anlegen (Base64-Image)
    suspend fun addDog(dog: Dog, context: Context) {
        val uid = auth.currentUser?.uid ?: return
        val imageBase64: String? = try {
            dog.imageUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?.let { Base64.encodeToString(it, Base64.NO_WRAP) }
            }
        } catch (_: Exception) {
            null
        }
        val data = mapOf(
            "id" to dog.id,
            "userId" to uid,
            "name" to dog.name,
            "age" to dog.age,
            "breed" to dog.breed,
            "description" to dog.description,
            "unavailableFrom" to dog.unavailableFrom,
            "unavailableTo" to dog.unavailableTo,
            "imageBase64" to imageBase64,
            "interestedUsers" to emptyList<String>()
        )
        db.collection("dogs").document(dog.id).set(data).await()
    }

    // Mahlzeiten aktualisieren
    suspend fun updateMeals(dogId: String, meals: Int) {
        db.collection("dogs").document(dogId).update("mealsGiven", meals).await()
    }

    // Spaziergänge aktualisieren
    suspend fun updateWalks(dogId: String, walks: Int) {
        db.collection("dogs").document(dogId).update("walksDone", walks).await()
    }

    // Hund löschen
    suspend fun deleteDog(dogId: String) {
        db.collection("dogs").document(dogId).delete().await()
    }

    // Hilfsfunktion zum Konvertieren eines Firestore-Dokuments in ein Dog-Objekt
    private fun docToDog(doc: DocumentSnapshot): Dog? {
        val id = doc.getString("id") ?: return null
        val name = doc.getString("name") ?: ""
        val age = doc.getString("age") ?: ""
        val breed = doc.getString("breed") ?: ""
        val description = doc.getString("description")
        val unavailableFrom = doc.getString("unavailableFrom")
        val unavailableTo = doc.getString("unavailableTo")
        val imageBase64 = doc.getString("imageBase64")
        val imageUriStr = imageBase64?.let { "data:image/jpeg;base64,$it" } ?: doc.getString("imageUri")
        val imageUri = imageUriStr?.let { Uri.parse(it) }
        // Load care metrics
        val mealsGiven = (doc.getLong("mealsGiven") ?: 0).toInt()
        val walksDone = (doc.getLong("walksDone") ?: 0).toInt()
        val totalWalks = (doc.getLong("totalWalks") ?: 0).toInt()
        val daysCared = (doc.getLong("daysCared") ?: 0).toInt()
        val totalCareDays = (doc.getLong("totalCareDays") ?: 0).toInt()
        return Dog(
            id = id,
            name = name,
            age = age,
            breed = breed,
            imageUri = imageUri,
            isCared = false,
            caretakerName = null,
            mealsGiven = mealsGiven,
            walksDone = walksDone,
            totalWalks = totalWalks,
            daysCared = daysCared,
            totalCareDays = totalCareDays,
            description = description,
            unavailableFrom = unavailableFrom,
            unavailableTo = unavailableTo
        )
    }
}
