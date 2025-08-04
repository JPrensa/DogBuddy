package de.syntax_institut.androidabschlussprojekt.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.viewmodel.EditProfileViewModel
import android.net.Uri
import java.util.UUID
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.FirestoreRepository

import android.widget.Toast
import android.util.Log
import android.util.Base64
import de.syntax_institut.androidabschlussprojekt.data.UserRepository
import de.syntax_institut.androidabschlussprojekt.model.Dog
import de.syntax_institut.androidabschlussprojekt.model.UserProfile
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import de.syntax_institut.androidabschlussprojekt.ui.components.FormSection
import de.syntax_institut.androidabschlussprojekt.ui.components.LabeledTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import de.syntax_institut.androidabschlussprojekt.ui.components.DogFormItem

private data class DogForm(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var age: String = "",
    var imageUri: Uri? = null
)


@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = viewModel()
) {
    val name = viewModel.name
    val email = viewModel.email
    val phone = viewModel.phone
    val age = viewModel.age
    val address = viewModel.address
        // Profilbild auswählen
        val currentProfile by FirestoreRepository.getUserProfileFlow().collectAsState(initial = UserProfile())
        var selectedProfileUri by remember { mutableStateOf<Uri?>(null) }
        val profileImageLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            selectedProfileUri = uri
        }
        val displayProfile: Any? = selectedProfileUri ?: currentProfile.imageUrl ?: R.drawable.baseline_image_24
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        var dogForms by remember { mutableStateOf(listOf<DogForm>()) }
        var currentPickerIndex by remember { mutableStateOf<Int?>(null) }
        val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            currentPickerIndex?.let { idx ->
                uri?.let { newUri ->
                    dogForms = dogForms.toMutableList().also { list -> list[idx] = list[idx].copy(imageUri = newUri) }
                }
            }
        }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (selectedProfileUri != null) {
            AsyncImage(
                model = selectedProfileUri,
                placeholder = painterResource(R.drawable.baseline_image_24),
                error = painterResource(R.drawable.baseline_image_24),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { profileImageLauncher.launch("image/*") }
            )
        } else if (currentProfile.imageUrl != null) {
            AsyncImage(
                model = currentProfile.imageUrl,
                placeholder = painterResource(R.drawable.baseline_image_24),
                error = painterResource(R.drawable.baseline_image_24),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { profileImageLauncher.launch("image/*") }
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.baseline_image_24),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { profileImageLauncher.launch("image/*") }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        FormSection("Profil bearbeiten")
        LabeledTextField(label = "Name", value = name, onValueChange = viewModel::onNameChange)
        LabeledTextField(label = "Email", value = email, onValueChange = viewModel::onEmailChange)
        LabeledTextField(label = "Telefon", value = phone, onValueChange = viewModel::onPhoneChange)
        LabeledTextField(label = "Alter", value = age, onValueChange = viewModel::onAgeChange)
        LabeledTextField(label = "Adresse", value = address, onValueChange = viewModel::onAddressChange)
        FormSection("Meine Hunde")
        dogForms.forEachIndexed { idx, form ->
            DogFormItem(
                name = form.name,
                age = form.age,
                imageUri = form.imageUri,
                onNameChange = { new -> dogForms = dogForms.toMutableList().also { it[idx] = it[idx].copy(name = new) } },
                onAgeChange = { new -> dogForms = dogForms.toMutableList().also { it[idx] = it[idx].copy(age = new) } },
                onPickImage = { currentPickerIndex = idx; imagePickerLauncher.launch("image/*") },
                onRemove = { dogForms = dogForms.toMutableList().also { it.removeAt(idx) } },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Button(
            onClick = { dogForms = dogForms + DogForm() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hund hinzufügen")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                                        val newImageUrl = selectedProfileUri
                        ?.let { UserRepository.uploadProfileImage(context, it) }
                        ?: currentProfile.imageUrl

                    val profile = UserProfile(
                        name = name,
                        email = email,
                        phone = phone,
                        age = age,
                        address = address,
                        imageUrl = newImageUrl
                    )
                    try {
                        UserRepository.updateUserProfile(profile)
                        
                        
                        
                        
                        
                        dogForms.forEach { form ->
                            FirestoreRepository.addDogBase64(
                                Dog(
                                    id = form.id,
                                    name = form.name,
                                    age = form.age,
                                    breed = "",
                                    imageUri = form.imageUri,
                                    description = null,
                                    unavailableFrom = null,
                                    unavailableTo = null
                                ),
                                context
                            )
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Profil gespeichert", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Fehler beim Speichern: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }

                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Speichern")
        }
        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Abbrechen")
        }
    }
}
