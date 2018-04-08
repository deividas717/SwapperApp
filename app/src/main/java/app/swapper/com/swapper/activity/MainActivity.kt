package app.swapper.com.swapper.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.MenuItem
import app.swapper.com.swapper.R
import app.swapper.com.swapper.SwaggerApp
import app.swapper.com.swapper.adapter.UserHorizontalGalleryAdapter
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.fragment.SwipeFragment
import app.swapper.com.swapper.model.UserItemsPresenterImpl
import app.swapper.com.swapper.presenter.UserItemsPresenter
import app.swapper.com.swapper.storage.SharedPreferencesManager
import app.swapper.com.swapper.view.UserItemsView
import com.facebook.AccessToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, UserItemsView {

    private lateinit var adapter : UserHorizontalGalleryAdapter
    private lateinit var userItemsPresenter: UserItemsPresenter
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val application = applicationContext as SwaggerApp
        user = application.getUser()

        userItemsPresenter = UserItemsPresenterImpl(this)
        userItemsPresenter.askServerForUserItems(user)

        val swipeFragment = supportFragmentManager.findFragmentById(R.id.swipeFragment) as SwipeFragment

        sendBtn.setOnClickListener {
            val selectedIds = adapter.getSelectedItemsIds()
            if (selectedIds.isNotEmpty()) {
                val activeCardId = swipeFragment.getActiveCardId()
                if (activeCardId != -1L) {
                    userItemsPresenter.sendItemExchangeRequest(activeCardId, selectedIds)
                    adapter.resetAllSelectableStates()
                }
            }
        }

        menuToggle.setOnClickListener {
            if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.openDrawer(Gravity.START)
            } else {
                drawer_layout.closeDrawer(Gravity.START)
            }
        }

        nav_view.setNavigationItemSelectedListener(this)
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

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {
                AccessToken.setCurrentAccessToken(null)
                startActivity(Intent(this, LoginActivity::class.java));
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun itemsLoaded(list: List<Item>) {
        adapter = UserHorizontalGalleryAdapter(applicationContext, list)
        userGalleryRecyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        userGalleryRecyclerView.adapter = adapter
    }

    fun getUser() : User? {
        return user
    }
}
