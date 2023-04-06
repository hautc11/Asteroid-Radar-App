package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.enums.Filter
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(private val asteroidRepository: AsteroidRepository) : ViewModel() {

    private var _imageOfDay = MutableLiveData<PictureOfDay>()
    val imageOfDay: LiveData<PictureOfDay>
        get() = _imageOfDay

    private val _asteroidFiler = MutableLiveData(Filter.TODAY)

    val asteroidsView: LiveData<List<Asteroid>> = _asteroidFiler.switchMap { filter ->
        when (filter) {
            Filter.TODAY -> {
                asteroidRepository.todayAsteroids
            }
            Filter.WEEK -> {
                asteroidRepository.weekAsteroids
            }
            else -> {
                asteroidRepository.savedAsteroids
            }
        }
    }

    fun getPictureOfDay() {
        viewModelScope.launch {
            _imageOfDay.value = asteroidRepository.getPictureOfDay()
        }
    }

    fun refreshAsteroids() {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
        }
    }

    fun displayWeekAsteroids() {
        _asteroidFiler.postValue(Filter.WEEK)
    }

    fun displayTodayAsteroids() {
        _asteroidFiler.postValue(Filter.TODAY)
    }

    fun displaySavedAsteroids() {
        _asteroidFiler.postValue(Filter.SAVED)
    }
    class Factory(private val asteroidRepository: AsteroidRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(asteroidRepository) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
