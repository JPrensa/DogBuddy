package de.syntax_institut.androidabschlussprojekt.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.DogDetailsViewModel
import de.syntax_institut.androidabschlussprojekt.data.api.Breed
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import coil.compose.rememberAsyncImagePainter

@Composable
fun BreedListScreen(viewModel: DogDetailsViewModel = viewModel()) {
    val breeds = viewModel.breeds
    var searchQuery by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Rasse suchen") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (breeds.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            var selectedBreed by remember { mutableStateOf<Breed?>(null) }
            val suggestions = if (searchQuery.isBlank()) emptyList() else breeds.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }.take(3)
            if (selectedBreed == null) {
                if (suggestions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Keine Rassen gefunden", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(suggestions) { breed ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedBreed = breed },
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AsyncImage(
                                        model = breed.image?.url ?: breed.referenceImageId?.let { "https://cdn2.thedogapi.com/images/$it.jpg" },
                                        contentDescription = breed.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = breed.name, style = MaterialTheme.typography.headlineSmall)
                                }
                            }
                        }
                    }
                }
            } else {
                val breed = selectedBreed!!
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            AsyncImage(
                                model = breed.image?.url ?: breed.referenceImageId?.let { "https://cdn2.thedogapi.com/images/$it.jpg" },
                                contentDescription = breed.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(350.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = breed.name, style = MaterialTheme.typography.headlineSmall)
                        }
                        breed.lifeSpan?.let {
                            Text(text = "Leben: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                        breed.temperament?.let {
                            Text(text = "Temperament: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                        breed.origin?.let {
                            Text(text = "Ursprung: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                        breed.weight?.metric?.let {
                            Text(text = "Gewicht: $it kg", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Button(
                    onClick = { selectedBreed = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Zurück")
                }
            }
        }
    }
}
