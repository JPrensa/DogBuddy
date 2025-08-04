package de.syntax_institut.androidabschlussprojekt.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.viewmodel.ProfileViewModel
import de.syntax_institut.androidabschlussprojekt.ui.components.Counter
import de.syntax_institut.androidabschlussprojekt.ui.components.DogAvatar
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect

@Composable
fun CareDetailScreen(
    navController: NavController,
    dogId: String,
    viewModel: ProfileViewModel = viewModel()
) {
    val dog = viewModel.caredDogs.find { it.id == dogId } ?: return
    // Real-time care period and owner info
    val db = Firebase.firestore
    var from by remember { mutableStateOf(dog.unavailableFrom ?: "") }
    var to by remember { mutableStateOf(dog.unavailableTo ?: "") }
    var ownerId by remember { mutableStateOf("") }
    DisposableEffect(dogId) {
        val reg = db.collection("dogs").document(dogId)
            .addSnapshotListener { snap, _ ->
                if (snap != null && snap.exists()) {
                    from = snap.getString("unavailableFrom") ?: ""
                    to = snap.getString("unavailableTo") ?: ""
                    ownerId = snap.getString("userId") ?: ""
                }
            }
        onDispose { reg.remove() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DogAvatar(dog = dog, size = 120.dp) { }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = dog.name, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        // Pflegezeit und Anfrager anzeigen
        if (from.isNotBlank() && to.isNotBlank()) {
            Text(text = "Betreuungszeit: $from bis $to", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (ownerId.isNotBlank()) {
            Text(text = "Anfrage von: $ownerId", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Counter(
            label = "Mahlzeiten gegeben:",
            count = dog.mealsGiven,
            onIncrement = { viewModel.updateMeals(dogId, dog.mealsGiven + 1) },
            onDecrement = { if (dog.mealsGiven > 0) viewModel.updateMeals(dogId, dog.mealsGiven - 1) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Counter(
            label = "Spaziergänge:",
            count = dog.walksDone,
            onIncrement = { viewModel.updateWalks(dogId, dog.walksDone + 1) },
            onDecrement = { if (dog.walksDone > 0) viewModel.updateWalks(dogId, dog.walksDone - 1) }
        )
    }
}
