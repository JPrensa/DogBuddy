package de.syntax_institut.androidabschlussprojekt.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.util.Base64
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.components.Background
import androidx.compose.ui.graphics.Color
import de.syntax_institut.androidabschlussprojekt.ui.Screen
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import de.syntax_institut.androidabschlussprojekt.ui.components.DogAvatar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete





@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    
    val dogs = viewModel.dogs
    val caredDogs = viewModel.caredDogs
    Background {
    
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Deine Hunde Anzeige", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dogs) { dog ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    Box(modifier = Modifier.size(80.dp)) {
                        DogAvatar(
                            dog = dog,
                            modifier = Modifier.fillMaxSize(),
                            onClick = { navController.navigate(Screen.DogProfile.createRoute(dog.id)) }
                        )
                        IconButton(
                            onClick = { viewModel.deleteDog(dog.id) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Löschen Hund",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dog.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Betreute Hunde", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(caredDogs) { dog ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    DogAvatar(
                        dog = dog,
                        modifier = Modifier.size(80.dp),
                        onClick = { navController.navigate(Screen.CareDetail.createRoute(dog.id)) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dog.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        val context = LocalContext.current
        // Profilbild anzeigen (Base64-dekodiert oder URL)
        val imageUriStr = viewModel.profileImageUrl
        if (imageUriStr?.startsWith("data:image/") == true) {
            val bitmap = remember {
                val decodedBytes = Base64.decode(imageUriStr.substringAfter(","), Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            }
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(328.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(16.dp)
            )
        } else {
            AsyncImage(
                model = viewModel.profileImageUrl ?: R.drawable.baseline_image_24,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(328.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = viewModel.name, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Alter: ${viewModel.age}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = viewModel.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Telefon: ${viewModel.phone}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Adresse: ${viewModel.address}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Anzahl Hunde: ${dogs.size}", style = MaterialTheme.typography.bodyMedium)

        OutlinedButton(
            onClick = { navController.navigate(Screen.EditProfile.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Profil bearbeiten")
        }
        
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Abmelden")
        }
    }
    }
}
