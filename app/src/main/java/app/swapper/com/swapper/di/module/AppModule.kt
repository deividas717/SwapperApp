package app.swapper.com.swapper.di.module

import android.content.Context
import app.swapper.com.swapper.SwapperApp
import app.swapper.com.swapper.networking.NetworkInterceptor
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.storage.SharedPreferencesManager
import app.swapper.com.swapper.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: SwapperApp): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providePrefs(context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(interceptor: NetworkInterceptor): OkHttpClient {
        val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(interceptor)
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constants.serverAddress)
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    fun provideItemService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}