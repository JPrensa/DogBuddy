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


import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.viewmodel.HomeViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.DogDetailViewModel
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
