package app.swapper.com.swapper.ui.activity

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import app.swapper.com.swapper.R
import app.swapper.com.swapper.State
import app.swapper.com.swapper.SwaggerApp
import app.swapper.com.swapper.databinding.ActivityMainBinding
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.events.*
import app.swapper.com.swapper.service.LocationService
import app.swapper.com.swapper.storage.SharedPreferencesManager
import app.swapper.com.swapper.swipableCard.TinderCardView
import app.swapper.com.swapper.ui.viewmodel.MainActivityViewModel
import app.swapper.com.swapper.ui.viewmodel.UserItemViewModel
import app.swapper.com.swapper.ui.viewmodel.factory.MainActivityViewModelFactory
import app.swapper.com.swapper.ui.viewmodel.factory.UserItemViewModelFactory
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.greenrobot.eventbus.Subscribe

class MainActivity : BaseActivity(),
        NavigationView.OnNavigationItemSelectedListener {

    private var location: Location? = null
    private var user: User? = null

    private val mainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        val swaggerApp = application as SwaggerApp
        val apiService = swaggerApp.getRetrofit()
        user = swaggerApp.getUser()
        ViewModelProviders.of(this, MainActivityViewModelFactory(apiService, user?.email)).get(MainActivityViewModel::class.java)
    }

    private val userViewModel by lazy(LazyThreadSafetyMode.NONE) {
        val swaggerApp = application as SwaggerApp
        val apiService = swaggerApp.getRetrofit()
        user = swaggerApp.getUser()
        ViewModelProviders.of(this, UserItemViewModelFactory(apiService, user)).get(UserItemViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        userViewModel.askServerForUserItems()
        binding.vm = userViewModel

        menuToggle.setOnClickListener {
            if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.openDrawer(Gravity.START)
            } else {
                drawer_layout.closeDrawer(Gravity.START)
            }
        }

        userViewModel.data.observe(this, android.arch.lifecycle.Observer { it?.let { handleData(it) } })

        nav_view.setNavigationItemSelectedListener(this)

        setUpHeaderData(nav_view.getHeaderView(0))
    }

    private fun handleData(data: List<Item>) {
        if (data.isNotEmpty()) {
            addCards(0, false)
        } else if (cardStackView.childCount == 0) {
            Toast.makeText(applicationContext, "No data", Toast.LENGTH_LONG).show()
        }
        cardsProgressBar.visibility = View.GONE
    }

    fun showPopup(v: View) {
        val popup = PopupMenu(applicationContext, v)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.popup_menu_swap -> {

                    }
                    R.id.popup_menu_buy -> {

                    }
                    R.id.popup_menu_all -> {

                    }
                }
                return true
            }
        })
        popup.show()
    }

    private fun addCards(cardsLeft: Int = 0, cardDismissed: Boolean = true) {
        val displayData = userViewModel.indexData(cardsLeft, cardDismissed)
        displayData?.forEach {
            cardStackView.addCard(TinderCardView(this, it, location))
        }
    }

    @Subscribe
    fun onLocationChanged(obj: LocationChangeEvent) {
        location = obj.location
        location?.let {
            userViewModel.changeLocation(it)
        }
    }

    @Subscribe
    fun onCardDismissedEvent(obj: OnCardDismissedEvent) {
        addCards(obj.count)
        userViewModel.resetAllSelectableStates()

//        val intent = Intent()
//        intent.action = Intent.ACTION_VIEW
//        intent.`package` = "com.facebook.orca"
//        intent.data = Uri.parse("https://m.me/" + "4")
//        startActivity(intent)

    }

    @Subscribe
    fun onUserDataLoaded(obj: OnUserDataLoaded) {
        userItemsProgressbar.visibility = View.GONE
    }

    @Subscribe
    fun onCardClickedEvent(obj: OnCardClickedEvent) {
        startActivity(DetailItemActivity.createNewIntent(applicationContext, obj.itemId))
    }

    private fun setUpHeaderData( navHeader: View) {
        user?.let {
            Glide.with(applicationContext).load(it.img).into(nav_view.getHeaderView(0).findViewById(R.id.profileImage))
            val name = navHeader.findViewById<TextView>(R.id.nameTextView)
            val email = navHeader.findViewById<TextView>(R.id.emailTextView)
            name.text = it.name
            email.text = it.email
        }
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
                startActivity(HistoryActivity.createNewIntent(applicationContext, location?.latitude, location?.longitude));
            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {
                logoutDialog()
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

    private fun handleDialogVisibility(state: State) {
//        if (show) {
//            val dialogFragment = MatchDialog()
//            dialogFragment.show(supportFragmentManager, "MatchDialog")
//            userViewModel.showDialog.value = false
//        }
        val handler = Handler()
        handler.postDelayed({
            when (state) {
                State.DELETE -> {
                    //sendBtn.setImageResource(R.drawable.ic_delete_black_24dp)
                }
                State.EDIT -> {
                    //sendBtn.setImageResource(R.drawable.ic_edit_black_24dp)
                }
                State.SEND -> {
                   // sendBtn.setImageResource(R.drawable.ic_send)
                }
            }
        }, 1000)
    }

    @Subscribe
    fun onUserClickOnHisItems(obj: SelectionEvent) {
        cardStackView.enableSwipeForCard(obj.isEmpty)
    }

    @Subscribe
    fun cardSwipeEvent(obj: CardSwipeAction) {
        if (obj.showShadow) {
            errorShadow.visibility = View.VISIBLE
        } else {
            errorShadow.visibility = View.GONE
        }
    }

    private fun logoutDialog() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val prefs = SharedPreferencesManager.getInstance(applicationContext)
                    prefs.clearAllData()
                    AccessToken.setCurrentAccessToken(null)
                    startActivity(Intent(this, LoginActivity::class.java))
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss()
                }
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout").setMessage("Are you sure you want to logout?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
    }
}
