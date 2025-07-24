package de.syntax_institut.androidabschlussprojekt.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.viewmodel.ProfileViewModel
import de.syntax_institut.androidabschlussprojekt.ui.components.Counter
import de.syntax_institut.androidabschlussprojekt.ui.components.DogAvatar

@Composable
fun CareDetailScreen(
    navController: NavController,
    dogId: String,
    viewModel: ProfileViewModel = viewModel()
) {
    val dog = viewModel.caredDogs.find { it.id == dogId } ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DogAvatar(dog = dog, size = 120.dp) { }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = dog.name, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        Counter(
            label = "Mahlzeiten gegeben:",
            count = dog.mealsGiven,
            onIncrement = { viewModel.updateMeals(dogId, dog.mealsGiven + 1) },
            onDecrement = { if (dog.mealsGiven > 0) viewModel.updateMeals(dogId, dog.mealsGiven - 1) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Counter(
            label = "Spaziergänge:",
            count = dog.walksDone,
            onIncrement = { viewModel.updateWalks(dogId, dog.walksDone + 1) },
            onDecrement = { if (dog.walksDone > 0) viewModel.updateWalks(dogId, dog.walksDone - 1) }
        )
    }
}
