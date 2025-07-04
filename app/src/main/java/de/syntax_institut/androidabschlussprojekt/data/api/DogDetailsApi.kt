package de.syntax_institut.androidabschlussprojekt.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

const val DOG_DETAILS_BASE_URL = "https://api.thedogapi.com/v1/"

private val dogDetailsMoshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val dogDetailsRetrofit = Retrofit.Builder()
    .baseUrl(DOG_DETAILS_BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(dogDetailsMoshi))
    .build()

@JsonClass(generateAdapter = true)
data class Weight(
    val imperial: String?,
    val metric: String?
)

@JsonClass(generateAdapter = true)
data class Height(
    val imperial: String?,
    val metric: String?
)

@JsonClass(generateAdapter = true)
data class BreedImage(
    val id: String?,
    val width: Int?,
    val height: Int?,
    val url: String?
)

@JsonClass(generateAdapter = true)
public data class Breed(
    val id: Int,
    val name: String,
    @Json(name = "life_span") val lifeSpan: String?,
    val temperament: String?,
    val origin: String?,
    val weight: Weight?,
    val height: Height?,
    @Json(name = "reference_image_id") val referenceImageId: String?,
    val image: BreedImage?
)

interface DogDetailsApiService {
    @GET("breeds")
    suspend fun getBreeds(): List<Breed>
}

object DogDetailsApi {
    val service: DogDetailsApiService by lazy { dogDetailsRetrofit.create(DogDetailsApiService::class.java) }
}
