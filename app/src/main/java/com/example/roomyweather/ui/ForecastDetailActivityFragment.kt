package com.example.roomyweather.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.roomyweather.R
import com.example.roomyweather.data.ForecastCity
import com.example.roomyweather.data.ForecastPeriod
import com.example.roomyweather.util.getTempUnitsDisplay
import com.example.roomyweather.util.getWindSpeedUnitsDisplay
import com.example.roomyweather.util.openWeatherEpochToDate
import java.text.SimpleDateFormat
import java.util.*

const val EXTRA_FORECAST_PERIOD = "com.example.android.roomyweather.FORECAST_PERIOD"
const val EXTRA_FORECAST_CITY = "com.example.android.roomyweather.FORECAST_CITY"


class ForecastDetailActivityFragment : Fragment(R.layout.activity_forecast_detail_fragment) {
    private val args: ForecastDetailActivityFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val forecastCity = args.forecastCity
        val forecastPeriod = args.forecastPeriod

        forecastCity?.let { city ->
            view.findViewById<TextView>(R.id.tv_forecast_city).text = city.name
        }

        forecastPeriod?.let { period ->
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val units = sharedPrefs.getString(getString(R.string.pref_units_key), null)
            val tempUnitsDisplay = getTempUnitsDisplay(units, requireContext())
            val windUnitsDisplay = getWindSpeedUnitsDisplay(units, requireContext())

            Glide.with(requireContext())
                .load(period.iconUrl)
                .into(view.findViewById(R.id.iv_forecast_icon))

            view.findViewById<TextView>(R.id.tv_forecast_date).text = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(openWeatherEpochToDate(period.epoch, forecastCity!!.tzOffsetSec).time)

            view.findViewById<TextView>(R.id.tv_low_temp).text = getString(
                R.string.forecast_temp,
                period.lowTemp,
                tempUnitsDisplay
            )

            view.findViewById<TextView>(R.id.tv_high_temp).text = getString(
                R.string.forecast_temp,
                period.highTemp,
                tempUnitsDisplay
            )

            view.findViewById<TextView>(R.id.tv_pop).text =
                getString(R.string.forecast_pop, period.pop)

            view.findViewById<TextView>(R.id.tv_clouds).text =
                getString(R.string.forecast_clouds, period.cloudCover)

            view.findViewById<TextView>(R.id.tv_wind).text = getString(
                R.string.forecast_wind,
                period.windSpeed,
                windUnitsDisplay
            )

            view.findViewById<ImageView>(R.id.iv_wind_dir).rotation =
                period.windDirDeg.toFloat()

            view.findViewById<TextView>(R.id.tv_forecast_description).text =
                period.description
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_forecast_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                shareForecastText()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareForecastText() {
        args.forecastCity?.let { city ->
            args.forecastPeriod?.let { period ->
                val date = openWeatherEpochToDate(period.epoch, city.tzOffsetSec)
                val shareText = getString(
                    R.string.share_forecast_text,
                    getString(R.string.app_name),
                    city.name,
                    getString(R.string.forecast_date_time, date),
                    period.description,
                    getString(R.string.forecast_temp, period.highTemp, "F"),
                    getString(R.string.forecast_temp, period.lowTemp, "F"),
                    getString(R.string.forecast_pop, period.pop)
                )

                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(intent, null))
            }
        }
    }
}