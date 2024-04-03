package com.example.roomyweather.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomyweather.R
import com.example.roomyweather.data.ForecastCity
import com.example.roomyweather.data.SavedCity

class SavedCitiesAdapter(private val onCityClick: (SavedCity) -> Unit) : RecyclerView.Adapter<SavedCitiesAdapter.ViewHolder>() {

    private var cities: List<SavedCity> = listOf()

    fun updateCities(newCities: List<SavedCity>) {
        cities = newCities
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cities[position], onCityClick)
    }

    override fun getItemCount(): Int = cities.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(city: SavedCity, onCityClick: (SavedCity) -> Unit) {
            // Set the properties of the view, for example, city name.
            itemView.setOnClickListener { onCityClick(city) }
        }
    }
}