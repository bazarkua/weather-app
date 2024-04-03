package com.example.roomyweather.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.roomyweather.BuildConfig
import com.example.roomyweather.R


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val viewModel: SavedCitiesViewModel by viewModels()
    private val unitsViewModel: FiveDayForecastViewModel by activityViewModels()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        initAddCityPreference()

        // Register the shared preference change listener
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the shared preference change listener
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun initAddCityPreference() {
        val addCityPreference: EditTextPreference? = findPreference("add_city_key")
        addCityPreference?.setOnPreferenceChangeListener { preference, newValue ->
            val cityName = newValue.toString()
            if (cityName.isNotBlank()) {
                viewModel.addCity(cityName)
                Toast.makeText(requireContext(), "City added", Toast.LENGTH_SHORT).show()
                true
            } else {
                Toast.makeText(requireContext(), "Please enter a city name", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "units_key") {
            val units = sharedPreferences.getString("units_key", "metric")
            val apiKey = BuildConfig.OPENWEATHER_API_KEY
            unitsViewModel.onUnitPreferenceChanged(units, apiKey)
        }
    }

    private fun updateMetricUnits() {
        // Your implementation to update the metric units
        unitsViewModel.unitChangeEvent.value = Unit
    }
}