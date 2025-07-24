package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.model.Dog

@Composable
fun DogAvatar(
    dog: Dog,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    onClick: () -> Unit
) {
    AsyncImage(
        model = dog.imageUri ?: R.drawable.baseline_pets_24,
        contentDescription = dog.name,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    )
}
