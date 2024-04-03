package com.example.roomyweather.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomyweather.BuildConfig
import com.example.roomyweather.R
import com.example.roomyweather.data.ForecastPeriod
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController


/*
 * Often, we'll have sensitive values associated with our code, like API keys, that we'll want to
 * keep out of our git repo, so random GitHub users with permission to view our repo can't see them.
 * The OpenWeather API key is like this.  We can keep our API key out of source control using the
 * technique described below.  Note that values configured in this way can still be seen in the
 * app bundle installed on the user's device, so this isn't a safe way to store values that need
 * to be kept secret at all costs.  This will only keep
 *
 * To use your own OpenWeather API key here, create a file called `gradle.properties` in your
 * GRADLE_USER_HOME directory (this will usually be `$HOME/.gradle/` in MacOS/Linux and
 * `$USER_HOME/.gradle/` in Windows), and add the following line:
 *
 *   OPENWEATHER_API_KEY="<put_your_own_OpenWeather_API_key_here>"
 *
 * Then, add the following line to the `defaultConfig` section of build.gradle:
 *
 *   buildConfigField("String", "OPENWEATHER_API_KEY", OPENWEATHER_API_KEY)
 *
 * The Gradle build for this project will grab that value and store it in the field
 * `BuildConfig.OPENWEATHER_API_KEY` that's used below.  You can read more about this setup on the
 * following pages:
 *
 *   https://developer.android.com/studio/build/gradle-tips#share-custom-fields-and-resource-values-with-your-app-code
 *
 *   https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties
 *
 * Alternatively, if you don't mind whether people see your OpenWeather API key on GitHub, you can
 * just hard-code your API key below, replacing `BuildConfig.OPENWEATHER_API_KEY` 🤷‍.
 */
const val OPENWEATHER_APPID = BuildConfig.OPENWEATHER_API_KEY

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_container
        ) as NavHostFragment
        val navController = navHostFragment.navController

        drawerLayout = findViewById(R.id.drawer_layout)
        appBarConfig = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfig)

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)

        addSavedCitiesToDrawer(navView, navController)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_container)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    private fun addSavedCitiesToDrawer(navView: NavigationView, navController: NavController) {
        val viewModel: SavedCitiesViewModel by viewModels()

        viewModel.savedCities.observe(this) { savedCities ->
            val sortedCities = savedCities.sortedByDescending { it.lastViewedTimestamp }
            val savedCitiesGroup = navView.menu.findItem(R.id.saved_cities_group)
            navView.menu.removeGroup(R.id.saved_cities_group) // Clear previous items in the group

            sortedCities.forEach { city ->
                val menuItem = navView.menu.add(R.id.saved_cities_group, Menu.NONE, Menu.NONE, city.name)
                menuItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_location) // Add location icon
                menuItem.setOnMenuItemClickListener {
                    navController.navigate(R.id.forecast_list, Bundle().apply {
                        putParcelable("city", city)
                    })
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }
    }
}