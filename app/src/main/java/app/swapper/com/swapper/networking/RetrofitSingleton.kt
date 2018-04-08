package app.swapper.com.swapper.networking

import app.swapper.com.swapper.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Deividas on 2018-04-07.
 */
class RetrofitSingleton private constructor() {
    companion object Factory {
        private var client = OkHttpClient.Builder()
                //.addInterceptor(AuthenticationInterceptor("deividas717@gmail.com", "deividas"))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

        private var retrofit = Retrofit.Builder()
                .baseUrl(Constants.serverAddress)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        var service = retrofit.create(ApiService::class.java)
    }
}