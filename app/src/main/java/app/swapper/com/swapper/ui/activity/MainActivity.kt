package app.swapper.com.swapper.ui.activity

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.*
import android.widget.TextView
import app.swapper.com.swapper.service.LocationService
import app.swapper.com.swapper.R
import app.swapper.com.swapper.SwaggerApp
import app.swapper.com.swapper.databinding.ActivityMainBinding
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.storage.SharedPreferencesManager
import app.swapper.com.swapper.ui.dialog.MatchDialog
import app.swapper.com.swapper.ui.factory.UserItemViewModelFactory
import app.swapper.com.swapper.ui.fragment.SwipeFragment
import app.swapper.com.swapper.ui.viewmodel.UserItemViewModel
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : BaseActivity(),
        NavigationView.OnNavigationItemSelectedListener {

    private lateinit var userViewModel: UserItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val swaggerApp = (application as SwaggerApp)
        val apiService = swaggerApp.getRetrofit()
        val user = swaggerApp.getUser()

        userViewModel = ViewModelProviders.of(this, UserItemViewModelFactory(apiService, user)).get(UserItemViewModel::class.java)
        userViewModel.askServerForUserItems()
        binding.vm = userViewModel

        val swipeFragment = supportFragmentManager.findFragmentById(R.id.swipeFragment) as SwipeFragment

        sendBtn.setOnClickListener {
            val activeCardId = swipeFragment.getActiveCardId()
            userViewModel.sendItemExchangeRequest(activeCardId)
        }

        menuToggle.setOnClickListener {
            if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.openDrawer(Gravity.START)
            } else {
                drawer_layout.closeDrawer(Gravity.START)
            }
        }

        userViewModel.showDialog.observe(this, android.arch.lifecycle.Observer {
            it?.let { handleDialogVisibility(it) }
        })

        nav_view.setNavigationItemSelectedListener(this)

        if (user != null) {
            setUpHeaderData(user, nav_view.getHeaderView(0))
        }
    }

    private fun setUpHeaderData(user: User, navHeader: View) {
        Glide.with(applicationContext).load(user.img).into(nav_view.getHeaderView(0).findViewById(R.id.profileImage))
        val name = navHeader.findViewById<TextView>(R.id.nameTextView)
        val email = navHeader.findViewById<TextView>(R.id.emailTextView)
        name.text = user.name
        email.text = user.email
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                startActivity(Intent(this, CreateNewItemActivity::class.java));
            }
            R.id.nav_gallery -> {
                startActivity(Intent(this, UserItemsActivity::class.java));
            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {
                val prefs = SharedPreferencesManager.getInstance(applicationContext)
                prefs.clearAllData()
                AccessToken.setCurrentAccessToken(null)
                startActivity(Intent(this, LoginActivity::class.java));
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPermissionGranted(grantedPermissions: Collection<String>) {
        if (Manifest.permission.ACCESS_FINE_LOCATION in grantedPermissions) {
            startService(Intent(this, LocationService::class.java))
        }
    }

    override fun onPermissionDenied(deniedPermissions: Collection<String>) {
        if (Manifest.permission.ACCESS_FINE_LOCATION in deniedPermissions) {

        }
    }

    private fun handleDialogVisibility(show: Boolean) {
        if (show) {
            val dialogFragment = MatchDialog()
            dialogFragment.show(supportFragmentManager, "MatchDialog")
            userViewModel.showDialog.value = false
        }
    }
}
