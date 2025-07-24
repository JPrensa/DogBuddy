package de.syntax_institut.androidabschlussprojekt.model

/**
 * Represents a user's profile data.
 */
data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val age: String = "",
    val address: String = "",
    val imageUrl: String? = null
)
