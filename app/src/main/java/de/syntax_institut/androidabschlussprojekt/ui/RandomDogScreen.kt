package de.syntax_institut.androidabschlussprojekt.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import de.syntax_institut.androidabschlussprojekt.viewmodel.DogViewModel

@Composable
fun RandomDogScreen(viewModel: DogViewModel = viewModel()) {
    val url = viewModel.imageUrl
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (url == null) {
            CircularProgressIndicator()
        } else {
            AsyncImage(
                model = url,
                contentDescription = "Zufälliges Hundebild",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(400.dp)
                    .height(350.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            val breedSegment = url.substringAfter("breeds/").substringBefore("/")
            val parts = breedSegment.split("-")
            val breedName = if (parts.size > 1) {
                "${parts[1].replaceFirstChar { it.uppercase() }} ${parts[0].replaceFirstChar { it.uppercase() }}"
            } else {
                parts[0].replaceFirstChar { it.uppercase() }
            }
            Text(text = "Rasse: $breedName", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.loadRandom() }) {
            Text("Neues Bild laden")
        }
    }
}
