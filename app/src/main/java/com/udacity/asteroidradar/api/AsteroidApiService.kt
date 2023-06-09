package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.domain.PictureOfDay
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidApiService {

    @GET("planetary/apod")
    suspend fun getPictureOfDay(): PictureOfDay

    @GET("neo/rest/v1/feed")
    suspend fun getNearEarthObjects(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): String
}
