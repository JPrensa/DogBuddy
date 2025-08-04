package de.syntax_institut.androidabschlussprojekt.ui

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
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
import de.syntax_institut.androidabschlussprojekt.ui.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import de.syntax_institut.androidabschlussprojekt.R
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
            // Verbleibende Tage anzeigen
            val daysLeft = runCatching {
                val formatter = DateTimeFormatter.ofPattern("d.M.yyyy")
                val toDate = LocalDate.parse(to, formatter)
                ChronoUnit.DAYS.between(LocalDate.now(), toDate).toInt()
            }.getOrNull()
            daysLeft?.let {
                Text(
                    text = if (it >= 0) "Noch $it Tage verbleibend" else "Betreuung beendet",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        if (ownerId.isNotBlank()) {
            var ownerName by remember { mutableStateOf("") }
            var ownerImageUrl by remember { mutableStateOf<String?>(null) }
            DisposableEffect(ownerId) {
                val ownerReg = db.collection("users").document(ownerId)
                    .addSnapshotListener { ownerSnap, _ ->
                        if (ownerSnap != null && ownerSnap.exists()) {
                            ownerName = ownerSnap.getString("name") ?: ""
                            ownerImageUrl = ownerSnap.getString("imageUrl")
                        }
                    }
                onDispose { ownerReg.remove() }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { navController.navigate(Screen.OwnerDetail.createRoute(ownerId)) }
            ) {
                AsyncImage(
                    model = ownerImageUrl ?: R.drawable.baseline_pets_24,
                    contentDescription = "Besitzer",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = ownerName, style = MaterialTheme.typography.headlineSmall)
            }
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
