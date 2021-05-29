package com.example.athro.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.athro.R
import com.example.athro.databinding.ActivityMainBinding
import com.example.athro.ui.login.signin.SigninActivity
import com.example.athro.ui.menu.settings.SettingsActivity
import com.example.athro.ui.onboarding.OnboardingActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            val intent = Intent(applicationContext, OnboardingActivity::class.java)
            startActivity(intent)
        }

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile, R.id.nav_tutors, R.id.nav_tutees, R.id.nav_log, R.id.nav_history
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        mAuth = FirebaseAuth.getInstance()

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mainViewModel.progUser.value = intent.getParcelableExtra("progUser")

        if (mainViewModel.progUser.value!!.tutor != null && mainViewModel.progUser.value!!.tutor!!.requests.isNotEmpty()) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channelID = "com.example.athro.requests"
            val channel = NotificationChannel(channelID, "Athro", importance)
            channel.description = "Athro Tutee Requests"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(channel)

            val goToIntent = Intent(applicationContext, MainActivity::class.java)
            goToIntent.putExtra("progUser", mainViewModel.progUser.value)
            val contentIntent: PendingIntent = PendingIntent.getActivity(
                applicationContext,
                100, goToIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder = Notification.Builder(this, channelID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.pending_tutee_requests))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)

            notificationManager.notify(1, builder.build())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_activity, menu)
        return true
    }

    override fun onBackPressed() {

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun openSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra("progUser", mainViewModel.progUser.value)
        startActivity(intent)
    }

    fun signout(item: MenuItem) {
        mainViewModel.progUser.value = null

        // Clear tutee request notif on logout.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)

        val intent = Intent(this, SigninActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Toast.makeText(applicationContext, getString(R.string.signed_out), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.close()

        // This part checks if current fragment is the same as destination.
        return if (findNavController(R.id.nav_host_fragment_content_main).currentDestination!!.id != item.itemId) {
            val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)

            // This part set proper pop up destination to prevent "looping" fragments.
            if (item.order and Menu.CATEGORY_SECONDARY == 0) {
                var startDestination: NavDestination? =
                    findNavController(R.id.nav_host_fragment_content_main).graph

                while (startDestination is NavGraph) {
                    val parent = startDestination
                    startDestination = parent.findNode(parent.startDestination)
                }

                builder.setPopUpTo(
                    startDestination!!.id,
                    false
                )
            }

            val options = builder.build()
            return try {
                findNavController(R.id.nav_host_fragment_content_main).navigate(
                    item.itemId,
                    null,
                    options
                )
                true
            } catch (e: IllegalArgumentException) // Couldn't find destination, do nothing.
            {
                false
            }
        } else {
            false
        }
    }
}
