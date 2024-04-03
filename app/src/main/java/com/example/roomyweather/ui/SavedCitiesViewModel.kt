package com.example.roomyweather.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.roomyweather.data.AppDatabase
import com.example.roomyweather.data.SavedCitiesRepository
import com.example.roomyweather.data.SavedCity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SavedCitiesViewModel(application: Application) : AndroidViewModel(application) {
    private val savedCity = SavedCitiesRepository(
        AppDatabase.getInstance(application).ForecastCityDao()
    )

    private val _savedCities = MutableLiveData<List<SavedCity>>()
    val savedCities: LiveData<List<SavedCity>> = _savedCities
    init {
        viewModelScope.launch {
            savedCity.getAllSavedCities().collect { cities ->
                _savedCities.value = cities
            }
        }
    }


    fun addCity(cityName: String) {
        viewModelScope.launch {
            if (!savedCity.cityExists(cityName)) {
                savedCity.insertCity(cityName)
                _savedCities.value = savedCity.getAllSavedCities().first()
            }
        }
    }
    fun updateCityTimestamp(cityName: String, timestamp: Long) {
        viewModelScope.launch {
            savedCity.updateCityTimestamp(cityName, timestamp)
        }
    }
}