package de.syntax_institut.androidabschlussprojekt.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import android.provider.MediaStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL = "https://dog.ceo/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

@JsonClass(generateAdapter = true)
data class RandomDogResponse(
    @Json(name = "message") val message: String,
    @Json(name = "status") val status: String
)

interface DogApiService {
    @GET("breeds/image/random")
    suspend fun getRandomDogImage(): RandomDogResponse
}

object DogApi {
    val service: DogApiService by lazy { retrofit.create(DogApiService::class.java) }
}




//interface APIService {
//}
//object MealsAPI {
//    val retrofitService: APIService by lazy { retrofit.create(APIService::class.java) }
//}
//
//
//
//suspend fun loadApiImages(): Images {
//    // Simuliert einen Download
//    Log.i("Info", "Download startet")
//    delay(3000)
//    Log.i("Info", "Download fertig")
//    return Images
//}
//fun fetchData() {
//    viewModelScope.launch {
//        val result = loadApiImages()
//        // Ergebnisse verarbeiten
//    }
//}

