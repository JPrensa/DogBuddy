package de.syntax_institut.androidabschlussprojekt.ui

import android.net.Uri
import android.widget.CalendarView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import de.syntax_institut.androidabschlussprojekt.viewmodel.UploadViewModel
import de.syntax_institut.androidabschlussprojekt.ui.Screen

@Composable
fun UploadScreen(
    navController: NavController,
    viewModel: UploadViewModel = viewModel()
) {
    val name = viewModel.name
    val age = viewModel.age
    val breed = viewModel.breed
    val description = viewModel.description
    val imageUri = viewModel.imageUri
    val unavailableFrom = viewModel.unavailableFrom
    val unavailableTo = viewModel.unavailableTo

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> viewModel.onImageUriChange(uri) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = age,
            onValueChange = viewModel::onAgeChange,
            label = { Text("Alter") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = breed,
            onValueChange = viewModel::onBreedChange,
            label = { Text("Rasse") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = viewModel::onDescriptionChange,
            label = { Text("Beschreibung") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Bild wählen")
        }
        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Gewähltes Bild",
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
            )
        }
        Text(text = "Nicht verfügbar von:", style = MaterialTheme.typography.bodyMedium)
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        viewModel.onUnavailableFromChange("$dayOfMonth.${month+1}.$year")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Text(text = "Von: $unavailableFrom", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Nicht verfügbar bis:", style = MaterialTheme.typography.bodyMedium)
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        viewModel.onUnavailableToChange("$dayOfMonth.${month+1}.$year")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Text(text = "Bis: $unavailableTo", style = MaterialTheme.typography.bodyMedium)
        Button(
            onClick = { viewModel.addDog { navController.navigate(Screen.Profile.route) } },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && age.isNotBlank() && breed.isNotBlank()
        ) {
            Text("Hund hinzufügen")
        }
    }
}
