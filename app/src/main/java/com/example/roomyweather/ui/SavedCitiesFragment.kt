package com.example.roomyweather.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomyweather.R
import com.example.roomyweather.data.ForecastCity
import com.example.roomyweather.data.ForecastPeriod
import com.example.roomyweather.data.SavedCity

class SavedCitiesFragment : Fragment(R.layout.saved_cities_fragment) {
    private val viewModel: SavedCitiesViewModel by viewModels()
    private val cityListAdapter = SavedCitiesAdapter(::onCityClick)
    private lateinit var savedCitiesRV: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedCitiesRV = view.findViewById(R.id.rv_saved_cities)
        savedCitiesRV.layoutManager = LinearLayoutManager(requireContext())
        savedCitiesRV.setHasFixedSize(true)
        savedCitiesRV.adapter = cityListAdapter

        viewModel.savedCities.observe(viewLifecycleOwner) {
            cityListAdapter.updateCities(it)
        }
    }

    private fun onCityClick(city: SavedCity) {
        val directions = SavedCitiesFragmentDirections.navigateToForecast(city)
        findNavController().navigate(directions)
    }
}