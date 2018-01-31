package alison.fivethingskotlin

import alison.fivethingskotlin.Fragments.AnalyticsFragment
import alison.fivethingskotlin.Fragments.DesignsFragment
import alison.fivethingskotlin.Fragments.FiveThingsFragment
import alison.fivethingskotlin.Fragments.SettingsFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper




class ContainerActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        setUpNavigationDrawer()

        if (savedInstanceState == null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out)
            fragmentTransaction.replace(R.id.content_frame, FiveThingsFragment())
            fragmentTransaction.commitAllowingStateLoss()
        }
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
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

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
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
        navigationView.setCheckedItem(R.id.five_things_item)
    }

    private fun loadFragment(fragment: android.support.v4.app.Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.content_frame, fragment)
        fragmentTransaction.commitAllowingStateLoss()
        drawerLayout.closeDrawers()
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}
