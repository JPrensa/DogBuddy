package de.syntax_institut.androidabschlussprojekt.data

import android.net.Uri
import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import de.syntax_institut.androidabschlussprojekt.model.UserProfile

object UserRepository {
    private val auth = Firebase.auth
    private val db = Firebase.firestore


    //Liefert ein Flow mit dem UserProfile aus Firestore

    fun getUserProfileFlow(): Flow<UserProfile> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val listener = db.collection("users").document(uid)
            .addSnapshotListener { snap, error ->
                if (error != null || snap == null || !snap.exists()) return@addSnapshotListener
                val profile = UserProfile(
                    name = snap.getString("name") ?: "",
                    email = snap.getString("email") ?: "",
                    phone = snap.getString("phone") ?: "",
                    age = snap.getString("age") ?: "",
                    address = snap.getString("address") ?: "",
                    imageUrl = snap.getString("imageUrl")
                )
                trySend(profile).isSuccess
            }
        awaitClose { listener.remove() }
    }


     //Aktualisiert das UserProfile in Firestore

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
        db.collection("users").document(uid).set(data).await()
    }


     //Lädt Profilbild als Base64 in Firestore und liefert Data-URI

    suspend fun uploadProfileImage(context: Context, imageUri: Uri): String? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val bytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() } ?: return null
            val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
            val dataUri = "data:image/jpeg;base64,$base64"
            db.collection("users").document(uid).update("imageUrl", dataUri).await()
            dataUri
        } catch (e: Exception) {
            Log.e("UserRepository", "uploadProfileImage (Base64) failed", e)
            null
        }
    }

    fun clear() {
        // keine lokalen States
    }
}
