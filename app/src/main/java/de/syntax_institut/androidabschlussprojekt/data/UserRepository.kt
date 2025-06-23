package de.syntax_institut.androidabschlussprojekt.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UserRepository {
    var name by mutableStateOf("Max Mustermann")
    var email by mutableStateOf("max@beispiel.de")
    var phone by mutableStateOf("030-123456")
    var age by mutableStateOf("")
    var address by mutableStateOf("")
}
