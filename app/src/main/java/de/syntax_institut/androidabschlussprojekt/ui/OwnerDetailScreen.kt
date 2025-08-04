package de.syntax_institut.androidabschlussprojekt.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun OwnerDetailScreen(
    navController: NavController,
    ownerId: String
) {
    val db = Firebase.firestore
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    DisposableEffect(ownerId) {
        val registration = db.collection("users").document(ownerId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    name = snapshot.getString("name") ?: ""
                    email = snapshot.getString("email") ?: ""
                    phone = snapshot.getString("phone") ?: ""
                    age = snapshot.getString("age") ?: ""
                    address = snapshot.getString("address") ?: ""
                    imageUrl = snapshot.getString("imageUrl")
                }
            }
        onDispose { registration.remove() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profilbild",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.baseline_person_24),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = name, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Alter: $age", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "E-Mail: $email", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Telefon: $phone", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Adresse: $address", style = MaterialTheme.typography.bodyMedium)
    }
}
