package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
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
        return try {
            withContext(Dispatchers.IO) {
                NetworkProvider.asteroidApiService.getPictureOfDay()
            }
        } catch (e: java.lang.Exception) {
            return PictureOfDay()
        }

    }

    suspend fun refreshAsteroids() {
        try {
            withContext(Dispatchers.IO) {
                val result = NetworkProvider.asteroidApiService.getNearEarthObjects(
                    startDate = startDate,
                    endDate = endDate
                )
                val parseResult = parseAsteroidsJsonResult(JSONObject(result)).toTypedArray()
                database.asteroidDAO.insertAll(*parseResult)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}
