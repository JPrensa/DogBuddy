package de.syntax_institut.androidabschlussprojekt.model

import android.net.Uri

data class Dog(
    val id: String,
    val name: String,
    val age: String,
    val breed: String,
    val imageUri: Uri?,
    val isCared: Boolean = false,
    val caretakerName: String? = null,
    val mealsGiven: Int = 0,
    val walksDone: Int = 0,
    val totalWalks: Int = 0,
    val daysCared: Int = 0,
    val totalCareDays: Int = 0,
    val description: String? = null,
    val unavailableFrom: String? = null,
    val unavailableTo: String? = null
)
