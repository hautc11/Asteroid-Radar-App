package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.domain.Asteroid

@Dao
interface AsteroidDAO {
    @Query("SELECT * FROM asteroids_table ORDER BY closeApproachDate")
    fun getAllAsteroid(): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate = :today ORDER BY closeApproachDate")
    fun getTodayAsteroids(today: String): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate BETWEEN :today AND :next7days ORDER BY closeApproachDate")
    fun getWeekAsteroids(today: String, next7days: String): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: Asteroid)
}
