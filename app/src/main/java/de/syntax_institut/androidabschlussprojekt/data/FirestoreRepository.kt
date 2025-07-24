package de.syntax_institut.androidabschlussprojekt.data

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import de.syntax_institut.androidabschlussprojekt.model.Dog
import de.syntax_institut.androidabschlussprojekt.model.UserProfile
import com.google.firebase.firestore.SetOptions

object FirestoreRepository {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val storage = Firebase.storage

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

    /** Dogs current user cares for **/
    fun getCaredDogsFlow(): Flow<List<Dog>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val listener = db.collection("dogs")
            .whereArrayContains("interestedUsers", uid)
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
                    val mealsGiven = (doc.getLong("mealsGiven") ?: 0).toInt()
                    val walksDone = (doc.getLong("walksDone") ?: 0).toInt()
                    Dog(id = id, name = name, age = age, breed = breed, imageUri = imageUri, isCared = true, mealsGiven = mealsGiven, walksDone = walksDone, description = description, unavailableFrom = unavailableFrom, unavailableTo = unavailableTo)
                }
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    /** Update meal count and notify owner **/
    suspend fun updateMeals(dogId: String, meals: Int) {
        val docRef = db.collection("dogs").document(dogId)
        docRef.update("mealsGiven", meals).await()
        val ownerId = docRef.get().await().getString("userId") ?: return
        val notification = mapOf(
            "toUserId" to ownerId,
            "dogId" to dogId,
            "type" to "meal",
            "count" to meals,
            "timestamp" to FieldValue.serverTimestamp()
        )
        db.collection("notifications").add(notification).await()
    }

    /** Update walk count and notify owner **/
    suspend fun updateWalks(dogId: String, walks: Int) {
        val docRef = db.collection("dogs").document(dogId)
        docRef.update("walksDone", walks).await()
        val ownerId = docRef.get().await().getString("userId") ?: return
        val notification = mapOf(
            "toUserId" to ownerId,
            "dogId" to dogId,
            "type" to "walk",
            "count" to walks,
            "timestamp" to FieldValue.serverTimestamp()
        )
        db.collection("notifications").add(notification).await()
    }

    /** User profile flow **/
    fun getUserProfileFlow(): Flow<UserProfile> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val listener = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val name = snapshot.getString("name") ?: ""
                val email = snapshot.getString("email") ?: ""
                val phone = snapshot.getString("phone") ?: ""
                val age = snapshot.getString("age") ?: ""
                val address = snapshot.getString("address") ?: ""
                val imageUrl = snapshot.getString("imageUrl")
                trySend(UserProfile(
                    name = name,
                    email = email,
                    phone = phone,
                    age = age,
                    address = address,
                    imageUrl = imageUrl
                ))
            }
        awaitClose { listener.remove() }
    }

    /** Update user profile **/
    suspend fun updateUserProfile(profile: UserProfile) {
        val uid = auth.currentUser?.uid ?: return
        val data = mapOf(
            "name" to profile.name,
            "email" to profile.email,
            "phone" to profile.phone,
            "age" to profile.age,
            "address" to profile.address,
            "imageUrl" to profile.imageUrl
        )
        db.collection("users").document(uid).set(data, SetOptions.merge()).await()
    }

    /** Upload and update profile image **/
    suspend fun uploadProfileImage(imageUri: Uri): String? {
        val uid = auth.currentUser?.uid ?: return null
        val storageRef = storage.reference.child("profile_images/$uid.jpg")
        storageRef.putFile(imageUri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()
        db.collection("users").document(uid)
            .set(mapOf("imageUrl" to downloadUrl), SetOptions.merge()).await()
        return downloadUrl
    }
}
