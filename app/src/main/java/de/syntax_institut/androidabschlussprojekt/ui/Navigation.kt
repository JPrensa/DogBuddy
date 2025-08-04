package de.syntax_institut.androidabschlussprojekt.ui

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.viewmodel.compose.viewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import de.syntax_institut.androidabschlussprojekt.ui.RegisterScreen
import de.syntax_institut.androidabschlussprojekt.ui.DogDetailScreen
import de.syntax_institut.androidabschlussprojekt.viewmodel.HomeViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
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
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.verticalScroll
import de.syntax_institut.androidabschlussprojekt.data.UserRepository
import de.syntax_institut.androidabschlussprojekt.ui.BreedListScreen
import de.syntax_institut.androidabschlussprojekt.ui.UploadScreen as MVVMUploadScreen
import de.syntax_institut.androidabschlussprojekt.ui.ProfileScreen as MVVMProfileScreen
import de.syntax_institut.androidabschlussprojekt.ui.EditProfileScreen as MVVMEditProfileScreen
import de.syntax_institut.androidabschlussprojekt.ui.CareDetailScreen
import de.syntax_institut.androidabschlussprojekt.ui.OwnerDetailScreen
import androidx.compose.foundation.rememberScrollState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import de.syntax_institut.androidabschlussprojekt.ui.components.Background

sealed class Screen(val route: String, val title: String) {
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Registrieren")
    object Home : Screen("home", "Home")
    object Upload : Screen("upload", "Upload")
    object Breeds : Screen("breeds", "Hunderassen")
    object Profile : Screen("profile", "Profile")
    object DogProfile : Screen("dog_profile/{dogId}", "Dog Profile") {
        fun createRoute(dogId: String) = "dog_profile/$dogId"
    }
    object CareDetail : Screen("care_detail/{dogId}", "Betreuungsübersicht") {
        fun createRoute(dogId: String) = "care_detail/$dogId"
    }
        object OwnerDetail : Screen("owner_detail/{ownerId}", "Besitzer") {
        fun createRoute(ownerId: String) = "owner_detail/$ownerId"
    }
    object EditProfile : Screen("edit_profile", "Profil bearbeiten")
}

@SuppressLint("ContextCastToActivity")
@Composable
fun AppNavHost() {
    val activity = LocalContext.current as ComponentActivity
    val loginViewModel: LoginViewModel = viewModel(activity)
    val isUserLoggedIn by loginViewModel.isLoggedIn
    val navController = rememberNavController()
    Background {
    if (!isUserLoggedIn) {
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        loginViewModel.login()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    viewModel = loginViewModel
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        loginViewModel.register()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = loginViewModel
                )
            }
        }
    } else {
        
            
            Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { BottomBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { HomeScreen(navController) }
                composable(Screen.Upload.route) { MVVMUploadScreen(navController) }
                composable(Screen.Breeds.route) { BreedListScreen() }
                composable(Screen.Profile.route) { MVVMProfileScreen(navController) }
                composable(Screen.EditProfile.route) { MVVMEditProfileScreen(navController) }
                composable(
                    Screen.DogProfile.route,
                    arguments = listOf(navArgument("dogId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val dogId = backStackEntry.arguments?.getString("dogId") ?: return@composable
                    DogDetailScreen(navController, dogId)
                }

                composable(
                    Screen.CareDetail.route,
                    arguments = listOf(navArgument("dogId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val dogId = backStackEntry.arguments?.getString("dogId") ?: return@composable
                    CareDetailScreen(navController, dogId)
                }
                composable(
                    Screen.OwnerDetail.route,
                    arguments = listOf(navArgument("ownerId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val ownerId = backStackEntry.arguments?.getString("ownerId") ?: return@composable
                    OwnerDetailScreen(navController, ownerId)
                }
            }
        }
    }
    }
}

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(Screen.Home, Screen.Upload, Screen.Breeds, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.50f),
        tonalElevation = 4.dp
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(
                            id = when (screen) {
                                Screen.Home -> R.drawable.baseline_home_24
                                Screen.Upload -> R.drawable.baseline_create_24
                                Screen.Profile -> R.drawable.baseline_person_24
                                Screen.Breeds -> R.drawable.baseline_filter_list_24
                                else -> R.drawable.baseline_home_24
                            }
                        ),
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    }
}



