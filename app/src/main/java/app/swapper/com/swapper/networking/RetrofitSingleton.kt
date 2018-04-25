package app.swapper.com.swapper.networking

import app.swapper.com.swapper.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.elvishew.xlog.XLog.addInterceptor



/**
 * Created by Deividas on 2018-04-07.
 */
class RetrofitSingleton private constructor() {
    companion object Factory {
        private var client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder().addHeader("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJTd2FwcGVyIiwic3ViIjoibWVuZGluc2tpc0BnbWFpbC5jb20iLCJhdWQiOiJtb2JpbGUiLCJpYXQiOjE1MjQ2NzYyNjAsImV4cCI6MTUzMjQ1MjI2MH0.ItiaeIaHMRKaYR1YcvCiT_9l_fHzYxjW_AVC0IDwjyp5Wa6cAnEG5VmcBK_nSGBvAd-fTPleMhvGUJ6s3JAN5A").build()
                    chain.proceed(request)
                }
                .build()

        private var retrofit = Retrofit.Builder()
                .baseUrl(Constants.serverAddress)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        var service: ApiService = retrofit.create(ApiService::class.java)
    }
}