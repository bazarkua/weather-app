package com.example.roomyweather.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomyweather.R
import com.example.roomyweather.data.ForecastPeriod
import com.example.roomyweather.data.SavedCity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar

class ForecastFragment : Fragment(R.layout.saved_forecast_fragment) {
    private val TAG = "ForecastFragment"

    private val viewModel: FiveDayForecastViewModel by viewModels()
    private val viewModelSavedCities: SavedCitiesViewModel by viewModels()
    private val forecastAdapter = ForecastAdapter(::onForecastItemClick)

    private lateinit var forecastListRV: RecyclerView
    private lateinit var loadingErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingErrorTV = view.findViewById(R.id.tv_loading_error)

        loadingIndicator = view.findViewById(R.id.loading_indicator)

        /*
         * Set up RecyclerView.
         */
        forecastListRV = view.findViewById(R.id.rv_forecast_list)
        forecastListRV.layoutManager = LinearLayoutManager(requireContext())
        forecastListRV.setHasFixedSize(true)
        forecastListRV.adapter = forecastAdapter

        /*
         * Set up an observer on the current forecast data. If the forecast is not null, display
         * it in the UI.
         */
        viewModel.forecast.observe(viewLifecycleOwner) { forecast ->
            if (forecast != null) {
                forecastAdapter.updateForecast(forecast)
                forecastListRV.visibility = View.VISIBLE
                forecastListRV.scrollToPosition(0)
                (activity as AppCompatActivity?)?.supportActionBar?.title = forecast.city.name
            }
        }

        /*
         * Set up an observer on the error associated with the current API call. If the error is
         * not null, display the error that occurred in the UI.
         */
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                loadingErrorTV.text = getString(R.string.loading_error, error.message)
                loadingErrorTV.visibility = View.VISIBLE
                Log.e(TAG, "Error fetching forecast: ${error.message}")
            }
        }

        /*
         * Set up an observer on the loading status of the API query. Display the correct UI
         * elements based on the current loading status.
         */
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                loadingIndicator.visibility = View.VISIBLE
                loadingErrorTV.visibility = View.INVISIBLE
                forecastListRV.visibility = View.INVISIBLE
            } else {
                loadingIndicator.visibility = View.INVISIBLE
            }
        }
    }


    override fun onResume() {
        super.onResume()

        val savedCity = arguments?.getParcelable<SavedCity>("city")
        val cityName = savedCity?.name ?: PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(getString(R.string.pref_city_key), "Corvallis,OR,US")
        if (cityName != null) {
            viewModelSavedCities.addCity(cityName)
            viewModelSavedCities.updateCityTimestamp(cityName, System.currentTimeMillis()) // Update the timestamp
        }
        val units = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(getString(R.string.pref_units_key), null)
        viewModel.loadFiveDayForecast(cityName, units, OPENWEATHER_APPID)
    }


    private fun onForecastItemClick(forecastPeriod: ForecastPeriod) {
        if (forecastPeriod != null) {
            val forecastCity = forecastAdapter.forecastCity
            val directions = ForecastFragmentDirections.navigateToForecastDetails(forecastCity, forecastPeriod)
            findNavController().navigate(directions)
        } else {
            Log.e(TAG, "Error: forecastPeriod is null in onForecastItemClick")
            // Optionally, you can show an error message to the user
        }
    }

    private fun viewForecastCityOnMap() {
        if (forecastAdapter.forecastCity != null) {
            val geoUri = Uri.parse(getString(
                R.string.geo_uri,
                forecastAdapter.forecastCity?.lat ?: 0.0,
                forecastAdapter.forecastCity?.lon ?: 0.0,
                11
            ))
            val intent = Intent(Intent.ACTION_VIEW, geoUri)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Snackbar.make(
                    requireView().findViewById(R.id.coordinator_layout),
                    R.string.action_map_error,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
}

