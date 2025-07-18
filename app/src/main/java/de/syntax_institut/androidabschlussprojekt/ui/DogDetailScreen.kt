package de.syntax_institut.androidabschlussprojekt.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FieldValue
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.viewmodel.HomeViewModel

@Composable
fun DogDetailScreen(
    navController: NavController,
    dogId: String
) {
    val homeViewModel: HomeViewModel = viewModel()
    val dogs by homeViewModel.dogs.collectAsState(initial = emptyList())
    val dog = dogs.find { it.id == dogId } ?: return
    val context = LocalContext.current
    val db = Firebase.firestore
    val auth = Firebase.auth
    var requested by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = dog.imageUri,
            contentDescription = dog.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(text = dog.name, style = MaterialTheme.typography.headlineSmall)
        Text(text = "Rasse: ${dog.breed}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Alter: ${dog.age}", style = MaterialTheme.typography.bodyMedium)
        if (!dog.unavailableFrom.isNullOrBlank() && !dog.unavailableTo.isNullOrBlank()) {
            Text(
                text = "Von ${dog.unavailableFrom} bis ${dog.unavailableTo}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        dog.description?.let { desc ->
            Text(text = desc, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (!requested) {
                    val uid = auth.currentUser?.uid ?: return@Button
                    db.collection("dogs").document(dogId)
                        .update("interestedUsers", FieldValue.arrayUnion(uid))
                        .addOnSuccessListener {
                            Toast.makeText(context, "Interesse bestätigt", Toast.LENGTH_SHORT).show()
                            requested = true
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Fehler beim Bestätigen", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            enabled = !requested,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (requested) "Bereits angefragt" else "Ich kann aufpassen")
        }
    }
}
