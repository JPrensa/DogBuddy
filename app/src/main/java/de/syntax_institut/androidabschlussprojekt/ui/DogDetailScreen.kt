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
import androidx.compose.foundation.Image
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import de.syntax_institut.androidabschlussprojekt.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip


import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.viewmodel.HomeViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.DogDetailViewModel
import de.syntax_institut.androidabschlussprojekt.data.FirestoreRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect

@Composable
fun DogDetailScreen(
    navController: NavController,
    dogId: String
) {
    val detailViewModel: DogDetailViewModel = viewModel()
    val requested by detailViewModel.requested.collectAsState(initial = false)
    val homeViewModel: HomeViewModel = viewModel()
    val dogs by homeViewModel.dogs.collectAsState(initial = emptyList())
    val dog = dogs.find { it.id == dogId } ?: return
    val context = LocalContext.current
    val userDogs by FirestoreRepository.getUserDogsFlow().collectAsState(initial = emptyList())
    val isOwner = userDogs.any { it.id == dogId }
    val db = Firebase.firestore
    var mealsGiven by remember { mutableStateOf(dog.mealsGiven) }
    var walksDone by remember { mutableStateOf(dog.walksDone) }
    var totalWalks by remember { mutableStateOf(dog.totalWalks) }
    var interestedUsers by remember { mutableStateOf<List<String>>(emptyList()) }
    DisposableEffect(dogId) {
        val registration = db.collection("dogs").document(dogId)
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null && snapshot.exists()) {
                    mealsGiven = (snapshot.getLong("mealsGiven") ?: 0).toInt()
                    walksDone = (snapshot.getLong("walksDone") ?: 0).toInt()
                    totalWalks = (snapshot.getLong("totalWalks") ?: 0).toInt()
                    interestedUsers = snapshot.get("interestedUsers") as? List<String> ?: emptyList()
                }
            }
        onDispose { registration.remove() }
    }
    LaunchedEffect(requested) {
        if (requested) {
            Toast.makeText(context, "Interesse bestätigt", Toast.LENGTH_SHORT).show()
        }
    }
    
    
    

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (dog.imageUri?.toString()?.startsWith("data:image") == true) {
            val uriStr = dog.imageUri.toString()
            val base64 = uriStr.substringAfter(",")
            val bitmap = remember(base64) {
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
            }
            Image(
                bitmap = bitmap,
                contentDescription = dog.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            AsyncImage(
                model = dog.imageUri,
                placeholder = painterResource(R.drawable.baseline_pets_24),
                error = painterResource(R.drawable.baseline_pets_24),
                contentDescription = dog.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Text(text = dog.name,
            style = MaterialTheme.typography.headlineSmall)

        Text(text = "Rasse: ${dog.breed}",
            style = MaterialTheme.typography.bodyMedium)

        Text(text = "Alter: ${dog.age}",
            style = MaterialTheme.typography.bodyMedium)
        if (!dog.unavailableFrom.isNullOrBlank() && !dog.unavailableTo.isNullOrBlank()) {
            Text(
                text = "Von ${dog.unavailableFrom} bis ${dog.unavailableTo}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        dog.description?.let { desc ->
            Text(
                text = "description: ${dog.description}",
                style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isOwner) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(3) { index ->
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_local_dining_24),
                        contentDescription = null,
                        tint = if (index < mealsGiven) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${mealsGiven}/3", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Spaziergänge", style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = walksDone.toFloat() / totalWalks.coerceAtLeast(1),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${walksDone}/${totalWalks}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Interessiert:", style = MaterialTheme.typography.bodyMedium)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                interestedUsers.forEach { uid ->
                    Text(text = uid, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (!isOwner) {
            Button(
                onClick = {
                    if (!requested) {
                        detailViewModel.requestCare(dogId)
                    }
                },
                enabled = !requested,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (requested) "Bereits angefragt" else "Ich kann aufpassen")
            }
        }
    }
}
