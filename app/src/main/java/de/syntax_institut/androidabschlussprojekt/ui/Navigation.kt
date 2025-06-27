package de.syntax_institut.androidabschlussprojekt.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.DogRepository
import de.syntax_institut.androidabschlussprojekt.model.Dog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import coil.compose.AsyncImage
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.navigation.navArgument
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.CalendarView
import androidx.navigation.NavType
import androidx.compose.foundation.Image
import androidx.compose.foundation.verticalScroll
import de.syntax_institut.androidabschlussprojekt.data.UserRepository
import de.syntax_institut.androidabschlussprojekt.ui.RandomDogScreen
import androidx.compose.foundation.rememberScrollState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Upload : Screen("upload", "Upload")
    object Calendar : Screen("Calendar", "Calendar")
    object RandomDog : Screen("random_dog", "Zufälliger Hund")
    object Profile : Screen("profile", "Profile")
    object DogProfile : Screen("dog_profile/{dogId}", "Dog Profile") {
        fun createRoute(dogId: String) = "dog_profile/$dogId"
    }
    object EditProfile : Screen("edit_profile", "Profil bearbeiten")
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val items = listOf(Screen.Home, Screen.Upload, Screen.Calendar, Screen.RandomDog, Screen.Profile)
            val navBackStackEntry = navController.currentBackStackEntryAsState().value
            val currentRoute = navBackStackEntry?.destination?.route
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = when(screen) {
                                        Screen.Home -> R.drawable.baseline_home_24
                                        Screen.Upload -> R.drawable.baseline_create_24
                                        Screen.Calendar -> R.drawable.baseline_calendar_month_24
                                        Screen.Profile -> R.drawable.baseline_person_24
                                        Screen.RandomDog -> R.drawable.baseline_pets_24
                                        else -> R.drawable.baseline_home_24
                                    }
                                ),
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if(currentRoute != screen.route) {
                                navController.navigate(screen.route)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Upload.route) { UploadScreen(navController) }
            composable(Screen.Calendar.route) { CalendarScreen() }
                composable(Screen.RandomDog.route) { RandomDogScreen() }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
            composable(
                Screen.DogProfile.route,
                arguments = listOf(navArgument("dogId") { type = NavType.StringType })
            ) { backStackEntry ->
                val dogId = backStackEntry.arguments?.getString("dogId")
                DogProfileScreen(dogId)
            }
            composable(Screen.EditProfile.route) {
                EditProfileScreen(navController)
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "DogBuddy", style = MaterialTheme.typography.headlineLarge)
        Button(
            onClick = { navController.navigate(Screen.Upload.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Upload")
        }
        Button(
            onClick = { navController.navigate(Screen.Calendar.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Map")
        }
        Button(
            onClick = { navController.navigate(Screen.Profile.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Profile")
        }
    }
}

@Composable
fun UploadScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> imageUri = uri }
        var description by remember { mutableStateOf("") }
        var unavailableFrom by remember { mutableStateOf("") }
        var unavailableTo by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Alter") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = breed,
            onValueChange = { breed = it },
            label = { Text("Rasse") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
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
                modifier = Modifier.size(128.dp)
            )
        }
        // Verfügbarkeitszeitraum
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Nicht verfügbar von:", style = MaterialTheme.typography.bodyMedium)
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        unavailableFrom = "$dayOfMonth.${month+1}.$year"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Text(text = "Von: $unavailableFrom", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Nicht verfügbar bis:", style = MaterialTheme.typography.bodyMedium)
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        unavailableTo = "$dayOfMonth.${month+1}.$year"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Text(text = "Bis: $unavailableTo", style = MaterialTheme.typography.bodyMedium)
        Button(
            onClick = {
                val id = System.currentTimeMillis().toString()
                DogRepository.addDog(Dog(
                        id,
                        name,
                        age,
                        breed,
                        imageUri,
                        description = description,
                        unavailableFrom = unavailableFrom,
                        unavailableTo = unavailableTo
                    ))
                navController.navigate(Screen.Profile.route)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && age.isNotBlank() && breed.isNotBlank()
        ) {
            Text("Hund hinzufügen")
        }
    }
}

@Composable
fun CalendarScreen() {
    var selectedDate by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Verfügbarkeits-Kalender", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        selectedDate = "$dayOfMonth.${month+1}.$year"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Ausgewähltes Datum: $selectedDate", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Deine Hunde", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DogRepository.dogs) { dog ->
                AsyncImage(
                    model = dog.imageUri ?: R.drawable.baseline_pets_24,
                    contentDescription = dog.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate(Screen.DogProfile.createRoute(dog.id)) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.profilbild1),
            contentDescription = "Profilbild",
            modifier = Modifier
                //.size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = UserRepository.name, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Alter: ${UserRepository.age}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = UserRepository.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Telefon: ${UserRepository.phone}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Adresse: ${UserRepository.address}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Anzahl Hunde: ${DogRepository.dogs.size}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = { navController.navigate(Screen.EditProfile.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Profil bearbeiten")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* TODO: Abmelden */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Abmelden")
        }
    }
}

@Composable
fun EditProfileScreen(navController: NavController) {
    var name by remember { mutableStateOf(UserRepository.name) }
    var email by remember { mutableStateOf(UserRepository.email) }
    var phone by remember { mutableStateOf(UserRepository.phone) }
    var age by remember { mutableStateOf(UserRepository.age) }
    var address by remember { mutableStateOf(UserRepository.address) }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Profil bearbeiten", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefon") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Alter") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Adresse") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                UserRepository.name = name
                UserRepository.email = email
                UserRepository.phone = phone
                UserRepository.age = age
                UserRepository.address = address
                navController.popBackStack()
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

@Composable
fun ScreenTemplate(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun DogProfileScreen(dogId: String?) {
    val dog = DogRepository.dogs.find { it.id == dogId } ?: return
    val now = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profilbild Hund
                val profileImageRes = when (dog.id) {
                    "1" -> R.drawable.dog1
                    "2" -> R.drawable.dog2
                    else -> R.drawable.baseline_pets_24
                }
                AsyncImage(
                    model = dog.imageUri ?: profileImageRes,
                    contentDescription = "Profilbild von ${dog.name}",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = dog.name, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Rasse: ${dog.breed}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Alter: ${dog.age}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                // Betreuung
                if (dog.isCared) {
                    // Profilbild Betreuer
                    val caretakerImageRes = when (dog.caretakerName) {
                        "Anna Schmidt" -> R.drawable.baseline_person_24
                        else -> R.drawable.baseline_person_24
                    }
                    Image(
                        painter = painterResource(id = caretakerImageRes),
                        contentDescription = "Profilbild Betreuer",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Betreut von: ${dog.caretakerName}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    // Betreuungstage
                    Text(text = "Tage betreut: ${dog.daysCared}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Verbleibende Tage: ${dog.totalCareDays - dog.daysCared}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    // Mahlzeiten
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(3) { index ->
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_local_dining_24),
                                contentDescription = null,
                                tint = if (index < dog.mealsGiven) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${dog.mealsGiven}/3", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Spaziergänge
                    Text(text = "Spaziergänge", style = MaterialTheme.typography.bodyMedium)
                    LinearProgressIndicator(
                        progress = dog.walksDone.toFloat() / dog.totalWalks.coerceAtLeast(1),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${dog.walksDone}/${dog.totalWalks}", style = MaterialTheme.typography.bodySmall)
                } else {
                    Text(text = "Bei mir zu Hause", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Erster Spaziergang
        Text(text = "Erster Spaziergang", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        AsyncImage(
            model = R.drawable.spazieren1,
            contentDescription = "Spaziergang",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Datum: ${now.format(dateFormatter)}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Uhrzeit: ${now.format(timeFormatter)}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
