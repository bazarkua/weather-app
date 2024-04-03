package com.example.roomyweather.data

class SavedCitiesRepository(private val dao: ForecastDao) {
    suspend fun insertCity(city: String) = dao.insertCity(SavedCity(name = city))
    fun getAllSavedCities() = dao.getAllSavedCities()
    suspend fun cityExists(cityName: String) = dao.cityExists(cityName)
    suspend fun updateCityTimestamp(cityName: String, timestamp: Long) {
        dao.updateCityTimestamp(cityName, timestamp)
    }
}