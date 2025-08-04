package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import android.util.Base64
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.model.Dog

@Composable
fun DogAvatar(
    dog: Dog,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    onClick: () -> Unit
) {
    val uriStr = dog.imageUri?.toString() ?: ""
    if (uriStr.startsWith("data:image/")) {
        val bitmap = remember {
            val decodedBytes = Base64.decode(uriStr.substringAfter(","), Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = dog.name,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(2.dp)
                .clickable(onClick = onClick)
        )
    } else {
        AsyncImage(
            model = uriStr.takeIf { it.isNotBlank() } ?: R.drawable.baseline_pets_24,
            placeholder = painterResource(R.drawable.baseline_pets_24),
            error = painterResource(R.drawable.baseline_pets_24),
            contentScale = ContentScale.Crop,
            contentDescription = dog.name,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
        )
    }
}
