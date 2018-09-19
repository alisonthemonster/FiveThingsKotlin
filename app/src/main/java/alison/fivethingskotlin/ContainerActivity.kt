package alison.fivethingskotlin

import alison.fivethingskotlin.adapter.FiveThingsAdapter
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
import android.view.inputmethod.InputMethodManager
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

    override fun selectDate(selectedDate: Date, isASearchResult: Boolean) {

        val daysBetween = Days.daysBetween(LocalDate(Date()), LocalDate(selectedDate)).days
        val newDateIndex = daysBetween + FiveThingsAdapter.STARTING_DAY
        val fragment = FiveThingsPagerFragment.newInstance(newDateIndex)

        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            replace(R.id.content_frame, fragment)
            if (isASearchResult) addToBackStack("search results")
            commitAllowingStateLoss()
        }
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
            selectDate(Date(), false)
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
        setSupportActionBar(toolbar)
        drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawerLayout.addDrawerListener(drawerListener)

        val drawerToggle = ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        title = ""
        navigation_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.five_things_item -> {
                    selectDate(Date(), false)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.analytics_item -> {
                    loadFragment(AnalyticsFragment())
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

    private val drawerListener = object : DrawerLayout.DrawerListener {

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            //Called when a drawer's position changes.
        }

        override fun onDrawerOpened(drawerView: View) {
            //Called when a drawer has settled in a completely open state.
            //The drawer is interactive at this point.
            // If you have 2 drawers (left and right) you can distinguish
            // them by using id of the drawerView. int id = drawerView.getId();
            // id will be your layout's id: for example R.id.left_drawer
        }

        override fun onDrawerClosed(drawerView: View) {
            // Called when a drawer has settled in a completely closed state.
        }

        override fun onDrawerStateChanged(newState: Int) {
            //possibly change to lose focus on all edit texts?
            // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

}
