package de.syntax_institut.androidabschlussprojekt.data

import androidx.compose.runtime.mutableStateListOf
import de.syntax_institut.androidabschlussprojekt.model.Dog

object DogRepository {
    val dogs = mutableStateListOf<Dog>()

    init {
        dogs.add(Dog("1", "Bella", "3 Jahre", "Labrador", null, isCared = true, caretakerName = "Anna Schmidt", mealsGiven = 2, walksDone = 1, totalWalks = 3, daysCared = 2, totalCareDays = 7))
        dogs.add(Dog("2", "Charlie", "2 Jahre", "Pudel", null, isCared = false))
    }

    fun addDog(dog: Dog) {
        dogs.add(dog)
    }
}
