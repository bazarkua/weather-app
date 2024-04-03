package com.example.roomyweather.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedCity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun ForecastCityDao(): ForecastDao

    companion object {
        @Volatile var instance: AppDatabase? = null

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "savedCities.db"
            ).build()

        fun getInstance(context: Context): AppDatabase{
            return instance?: synchronized(this){
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }
    }
}