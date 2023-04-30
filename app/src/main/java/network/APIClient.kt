package network

import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object APIClient {
//    The base url which will be required by the retrofit builder
    private val BASE_URL = "https://newsapi.org/"
//    Creating a variable for the moshi builder and adding moshi to it
//    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val gson = GsonBuilder()
        .setLenient()
        .create()
//    Instantiating Retrofit.
    private val retrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

    val apiService: ApiService by lazy{
        retrofit.create(ApiService::class.java)
    }
interface ApiService{
    @GET("v2/top-headlines")
    fun fetchHeadlines(
        @Query("country") country:String,
        @Query("apiKey") apiKey: String
    ):Call<ArticleResponse>
}


}
