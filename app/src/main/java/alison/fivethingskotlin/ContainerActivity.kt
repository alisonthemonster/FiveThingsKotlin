package alison.fivethingskotlin

import alison.fivethingskotlin.fragment.*
import alison.fivethingskotlin.util.AlarmBootReceiver
import alison.fivethingskotlin.util.NotificationScheduler
import alison.fivethingskotlin.util.clearAuthState
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_container.*
import org.joda.time.Days
import org.joda.time.LocalDate
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*


class ContainerActivity : AppCompatActivity(), SearchFragment.OnDateSelectedListener {

    companion object {
        const val CHANNEL_ID = "FiveThingsChannel"
    }

    private lateinit var drawerLayout: DrawerLayout

    override fun onDateSelected(currentDate: Date, selectedDate: Date) {

        val daysBetween = Days.daysBetween(LocalDate(Date()), LocalDate(selectedDate)).days


        val newDateIndex = daysBetween + 25
        val fragment = DesignsFragment.newInstance(newDateIndex)

        //TODO this approach loses the backstack for the search results

        supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.content_frame, fragment)
//                .addToBackStack("search results")
                .commitAllowingStateLoss()
        navigation_view.setCheckedItem(R.id.five_things_item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val isLightMode = sharedPref.getBoolean("dark_light_mode", false) //default is dark mode

        if (isLightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //LIGHT MODE
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) //DARK MODE
        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_container)

        setUpNavigationDrawer()

        if (savedInstanceState == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out)
            fragmentTransaction.replace(R.id.content_frame, DesignsFragment())
            fragmentTransaction.commitAllowingStateLoss()
        }

        createNotificationChannel()

        val receiver = ComponentName(this, AlarmBootReceiver::class.java)

        packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        )

        NotificationScheduler().setReminderNotification(this)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // The action bar home/up action should open or close the drawer.
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            fragmentManager.backStackEntryCount > 0 -> fragmentManager.popBackStack()
            else -> super.onBackPressed()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun setUpNavigationDrawer() {
        //TODO come back and replace with view binding?
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val drawerToggle = ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close)
        drawerLayout.addDrawerListener(drawerToggle)
        title = ""
        drawerToggle.syncState()

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.five_things_item -> {
                    loadFragment(FiveThingsFragment())
                    true
                }
                R.id.analytics_item -> {
                    loadFragment(AnalyticsFragment())
                    true
                }

                R.id.templates_item -> {
                    loadFragment(DesignsFragment())
                    true
                }
                R.id.search_item -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.settings_item -> {
                    loadFragment(SettingsFragment())
                    true
                }
                R.id.logout_item -> {
                    logOut()
                    true
                }
                else -> {
                    true
                }
            }
        }
        navigation_view.setCheckedItem(R.id.five_things_item)
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.content_frame, fragment)
        fragmentTransaction.commitAllowingStateLoss()
        drawerLayout.closeDrawers()
    }

    private fun logOut() {
        clearAuthState(this)

        val intent = Intent(applicationContext, PromoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(ContainerActivity.CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}
