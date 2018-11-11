package alison.fivethingskotlin

import alison.fivethingskotlin.adapter.FiveThingsAdapter
import alison.fivethingskotlin.fragment.AnalyticsFragment
import alison.fivethingskotlin.fragment.FiveThingsPagerFragment
import alison.fivethingskotlin.fragment.SearchFragment
import alison.fivethingskotlin.fragment.SettingsFragment
import alison.fivethingskotlin.util.*
import android.app.ActionBar
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.preference.PreferenceManager
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ActionMenuView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_container.*
import kotlinx.android.synthetic.main.fragment_five_things.*
import org.joda.time.Days
import org.joda.time.LocalDate
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*


class ContainerActivity : AppCompatActivity(), SearchFragment.OnDateSelectedListener {

    companion object {
        const val CHANNEL_ID = "FiveThingsChannel"
    }

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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val isLightMode = sharedPref.getBoolean("dark_light_mode", true) //default is light mode

        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        if (isLightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //LIGHT MODE
        } else {
            firebaseAnalytics.setUserProperty("DarkModeUser", "Dark mode user")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) //DARK MODE
        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_container)

        setUpNavigationDrawer()

        setUpCalendar()

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.five_things_item -> {
                selectDate(Date(), false)
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
            else -> {
                true
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun setUpCalendar() {
        compactcalendar_view.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                binding.loading = true
                selectDate(dateClicked, false)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                currentDate = firstDayOfNewMonth
                binding.month = getMonth(firstDayOfNewMonth) + " " + getYear(firstDayOfNewMonth)
            }
        })

        todayButton.setOnClickListener {
            binding.loading = true
            val activity = context as ContainerActivity
            activity.selectDate(Date(), false)
        }
    }

    private fun setUpSpacedToolbar() {
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)

        bottom_app_bar.inflateMenu(R.menu.nav_menu)
        bottom_app_bar.setContentInsetsAbsolute(10, 10)

        val screenWidth = metrics.widthPixels
        val toolbarParams = Toolbar.LayoutParams(screenWidth, ActionBar.LayoutParams.WRAP_CONTENT)

        for (i in 0..4) {
            val childView = bottom_app_bar.getChildAt(i)
            if (childView is ViewGroup) {
                childView.layoutParams = toolbarParams
                val innerChildCount = childView.childCount
                val itemWidth = (screenWidth / innerChildCount)
                val params = ActionMenuView.LayoutParams(itemWidth, ActionBar.LayoutParams.WRAP_CONTENT)
                for (j in 0..innerChildCount) {
                    val grandChild = childView.getChildAt(j)
                    if (grandChild is ActionMenuItemView) {
                        grandChild.layoutParams = params
                    }
                }
            }
        }
    }

    private fun setUpNavigationDrawer() {
        setSupportActionBar(bottom_app_bar)
        setUpSpacedToolbar()
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.content_frame, fragment)
        fragmentTransaction.commitAllowingStateLoss()
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
