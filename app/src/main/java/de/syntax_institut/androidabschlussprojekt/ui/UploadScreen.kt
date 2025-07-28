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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import coil.compose.AsyncImage
import de.syntax_institut.androidabschlussprojekt.viewmodel.UploadViewModel
import de.syntax_institut.androidabschlussprojekt.ui.Screen
import de.syntax_institut.androidabschlussprojekt.ui.components.FormTextField
import de.syntax_institut.androidabschlussprojekt.ui.components.ImagePicker
import de.syntax_institut.androidabschlussprojekt.ui.components.DateSelector
import de.syntax_institut.androidabschlussprojekt.ui.components.FullWidthButton

@Composable
fun UploadScreen(
    navController: NavController,
    viewModel: UploadViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
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
        FormTextField(
            value = name,
            onValueChange = viewModel::onNameChange,
            label = "Name"
        )
        FormTextField(
            value = age,
            onValueChange = viewModel::onAgeChange,
            label = "Alter"
        )
        FormTextField(
            value = breed,
            onValueChange = viewModel::onBreedChange,
            label = "Rasse"
        )
        FormTextField(
            value = description,
            onValueChange = viewModel::onDescriptionChange,
            label = "Beschreibung"
        )
        ImagePicker(
            model = imageUri,
            onPick = viewModel::onImageUriChange,
            modifier = Modifier
                .size(128.dp)
                .clip(MaterialTheme.shapes.small)
                
        )
        
        DateSelector(
            label = "Nicht verfügbar von:",
            selectedDate = unavailableFrom,
            onDateChange = viewModel::onUnavailableFromChange
        )

        DateSelector(
            label = "Nicht verfügbar bis:",
            selectedDate = unavailableTo,
            onDateChange = viewModel::onUnavailableToChange
        )

        FullWidthButton(
            onClick = {
                coroutineScope.launch {
                    val id = System.currentTimeMillis().toString()
                    val dog = de.syntax_institut.androidabschlussprojekt.model.Dog(
                        id = id,
                        name = viewModel.name,
                        age = viewModel.age,
                        breed = viewModel.breed,
                        imageUri = viewModel.imageUri,
                        description = viewModel.description,
                        unavailableFrom = viewModel.unavailableFrom,
                        unavailableTo = viewModel.unavailableTo
                    )
                    try {
                        de.syntax_institut.androidabschlussprojekt.data.FirestoreRepository.addDogBase64(dog, context)
                        android.widget.Toast.makeText(context, "Hund gespeichert", android.widget.Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "Fehler: " + (e.localizedMessage ?: "Unbekannter Fehler"), android.widget.Toast.LENGTH_LONG).show()
                        android.util.Log.e("UploadScreen", "Error adding dog", e)
                    }
                    navController.popBackStack()
                }
            },
            text = "Hund hinzufügen",
            enabled = name.isNotBlank() && age.isNotBlank() && breed.isNotBlank()
        )
    }
}
