package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import de.syntax_institut.androidabschlussprojekt.R
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Counter(
    label: String,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        IconButton(onClick = onDecrement) {
            Icon(
    painter = painterResource(id = R.drawable.baseline_remove_24),
    contentDescription = "Decrement"
)
        }
        Text(text = count.toString(), style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onIncrement) {
            Icon(
    painter = painterResource(id = R.drawable.baseline_add_24),
    contentDescription = "Increment"
)
        }
    }
}
