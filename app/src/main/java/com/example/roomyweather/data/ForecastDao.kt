package com.example.roomyweather.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {
    //insert and delete queries
    @Insert
    suspend fun insertCity(forecastCity: SavedCity)

    @Query("SELECT * FROM SavedCity")
    fun getAllSavedCities(): Flow<List<SavedCity>>

    @Query("SELECT EXISTS(SELECT * FROM SavedCity WHERE name = :cityName)")
    suspend fun cityExists(cityName: String): Boolean

    @Query("UPDATE SavedCity SET lastViewedTimestamp = :timestamp WHERE name = :cityName")
    suspend fun updateCityTimestamp(cityName: String, timestamp: Long)
}