package com.example.core.network

import com.example.core.datastore.UserPreferences
import com.example.core.network.dto.RefreshTokenRequestDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    private const val DEFAULT_BASE_URL = "https://timy-api.kisiiuniversity.ac.ke/"

    fun createMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    fun createAuthInterceptor(userPreferences: UserPreferences): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()

            // Skip auth header for login or refresh endpoints
            val path = originalRequest.url.encodedPath
            if (path.contains("/auth/login") || path.contains("/auth/refresh")) {
                return@Interceptor chain.proceed(originalRequest)
            }

            val token = runBlocking { userPreferences.accessToken.firstOrNull() }
            val requestBuilder = originalRequest.newBuilder()
            if (!token.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
            requestBuilder.header("Accept", "application/json")
            requestBuilder.header("User-Agent", "Timy-Android/1.0 (Kisii University)")

            chain.proceed(requestBuilder.build())
        }
    }

    fun createTokenAuthenticator(
        userPreferences: UserPreferences,
        baseUrl: String = DEFAULT_BASE_URL
    ): Authenticator {
        return object : Authenticator {
            private val lock = Any()

            override fun authenticate(route: Route?, response: Response): Request? {
                // If we already tried refreshing once and failed, abort to avoid infinite loop
                if (responseCount(response) >= 2) {
                    return null
                }

                synchronized(lock) {
                    val currentToken = runBlocking { userPreferences.accessToken.firstOrNull() }
                    val headerToken = response.request.header("Authorization")?.removePrefix("Bearer ")

                    // If token changed while another thread refreshed, retry with new token
                    if (currentToken != null && currentToken != headerToken) {
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer $currentToken")
                            .build()
                    }

                    // Otherwise execute refresh using dedicated bare OkHttp client
                    val refreshToken = runBlocking { userPreferences.refreshToken.firstOrNull() } ?: return null

                    try {
                        val bareClient = OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .build()

                        val refreshRetrofit = Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .client(bareClient)
                            .addConverterFactory(MoshiConverterFactory.create(createMoshi()))
                            .build()

                        val refreshService = refreshRetrofit.create(TimyApiService::class.java)
                        val refreshResponse = runBlocking {
                            refreshService.refresh(RefreshTokenRequestDto(refreshToken))
                        }

                        if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                            val newTokens = refreshResponse.body()!!
                            runBlocking {
                                userPreferences.saveSession(
                                    accessToken = newTokens.accessToken,
                                    refreshToken = newTokens.refreshToken ?: refreshToken,
                                    userId = "",
                                    regNo = "",
                                    name = "",
                                    cohortId = "",
                                    cohortName = ""
                                )
                            }
                            return response.request.newBuilder()
                                .header("Authorization", "Bearer ${newTokens.accessToken}")
                                .build()
                        } else {
                            runBlocking { userPreferences.clearSession() }
                            return null
                        }
                    } catch (e: Exception) {
                        return null
                    }
                }
            }

            private fun responseCount(response: Response): Int {
                var result = 1
                var prior = response.priorResponse
                while (prior != null) {
                    result++
                    prior = prior.priorResponse
                }
                return result
            }
        }
    }

    fun createApiService(
        userPreferences: UserPreferences,
        baseUrl: String = DEFAULT_BASE_URL
    ): TimyApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(createAuthInterceptor(userPreferences))
            .addInterceptor(loggingInterceptor)
            .authenticator(createTokenAuthenticator(userPreferences, baseUrl))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(createMoshi()))
            .build()

        return retrofit.create(TimyApiService::class.java)
    }
}
