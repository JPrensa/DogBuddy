package de.syntax_institut.androidabschlussprojekt.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.viewmodel.EditProfileViewModel

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

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Profil bearbeiten", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = viewModel::onPhoneChange,
            label = { Text("Telefon") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = age,
            onValueChange = viewModel::onAgeChange,
            label = { Text("Alter") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address,
            onValueChange = viewModel::onAddressChange,
            label = { Text("Adresse") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.save { navController.popBackStack() } },
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
