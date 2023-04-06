package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.utils.Constants
import com.udacity.asteroidradar.api.NetworkProvider
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AsteroidRepository(private val database: AsteroidDatabase) {

    private val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))

    val todayAsteroids: LiveData<List<Asteroid>> = database.asteroidDAO.getTodayAsteroids(today = startDate)
    val weekAsteroids: LiveData<List<Asteroid>> = database.asteroidDAO.getWeekAsteroids(today = startDate, next7days = endDate)
    val savedAsteroids: LiveData<List<Asteroid>> = database.asteroidDAO.getAllAsteroid()

    suspend fun getPictureOfDay(): PictureOfDay {
        return withContext(Dispatchers.IO) {
            NetworkProvider.asteroidApiService.getPictureOfDay(
                apiKey = Constants.API_KEY
            )
        }
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val result = NetworkProvider.asteroidApiService.getNearEarthObjects(
                startDate = startDate,
                endDate = endDate,
                apiKey = Constants.API_KEY
            )
            val parseResult = parseAsteroidsJsonResult(JSONObject(result)).toTypedArray()
            database.asteroidDAO.insertAll(*parseResult)
        }
    }
}
