package com.udacity.asteroidradar.api

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.utils.Constants
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun parseAsteroidsJsonResult(jsonObject: JSONObject): List<Asteroid> {
    val asteroidList = mutableListOf<Asteroid>()
    val nearEarthObjectsJson = jsonObject.getJSONObject("near_earth_objects")
    val dateList = nearEarthObjectsJson.keys()
    val dateListSorted = dateList.asSequence().sorted()
    dateListSorted.forEach {
        val key: String = it
        val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(key)
        for (i in 0 until dateAsteroidJsonArray.length()) {
            val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
            val id = asteroidJson.getLong("id")
            val codename = asteroidJson.getString("name")
            val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
            val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                .getJSONObject("kilometers").getDouble("estimated_diameter_max")
            val closeApproachData = asteroidJson
                .getJSONArray("close_approach_data").getJSONObject(0)
            val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                .getDouble("kilometers_per_second")
            val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                .getDouble("astronomical")
            val isPotentiallyHazardous = asteroidJson
                .getBoolean("is_potentially_hazardous_asteroid")
            val asteroid = Asteroid(
                id,
                codename,
                key,
                absoluteMagnitude,
                estimatedDiameter,
                relativeVelocity,
                distanceFromEarth,
                isPotentiallyHazardous
            )
            asteroidList.add(asteroid)
        }
    }
    return asteroidList
}

fun ImageView.loadImageUrl(
    originUrl: String,
    context: Context,
    onSuccessLoad: () -> Unit = {}
) {
    Picasso.Builder(context).build().load(originUrl).into(this, object : Callback {
        override fun onSuccess() {
            onSuccessLoad.invoke()
        }

        override fun onError() {}
    })
}
