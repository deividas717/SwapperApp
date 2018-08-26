package app.swapper.com.swapper.networking

import app.swapper.com.swapper.storage.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkInterceptor @Inject constructor(private val prefs: SharedPreferencesManager): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", prefs.getAccessToken().accessToken)
                .build()
        return chain.proceed(request)
    }
}