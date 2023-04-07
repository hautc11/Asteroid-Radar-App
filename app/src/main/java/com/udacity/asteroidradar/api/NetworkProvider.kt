package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object NetworkProvider {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okhttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val url = chain
                .request()
                .url
                .newBuilder()
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .build()
            chain.proceed(chain.request().newBuilder().url(url).build())
        }
        .build()

    val asteroidApiService: AsteroidApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okhttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AsteroidApiService::class.java)
    }
}
