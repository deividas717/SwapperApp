package app.swapper.com.swapper.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import app.swapper.com.swapper.R
import app.swapper.com.swapper.SwapperApp
import app.swapper.com.swapper.databinding.ActivityUserItemsBinding
import app.swapper.com.swapper.events.OnCardClickedEvent
import app.swapper.com.swapper.ui.viewmodel.factory.UserItemsGalleryViewModel
import app.swapper.com.swapper.ui.viewmodel.UserProfileItemViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class UserItemsActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserProfileItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val swaggerApp = (application as SwapperApp)
        val apiService = swaggerApp.getRetrofit()
        val user = swaggerApp.getUser()

        userViewModel = ViewModelProviders.of(this, UserItemsGalleryViewModel(apiService, user)).get(UserProfileItemViewModel::class.java)
        val binding: ActivityUserItemsBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_items)
        binding.viewModel = userViewModel

        userViewModel.askServerForUserItems()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.user_gallery_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> {

            }
            R.id.action_add -> {
                startActivity(CreateNewItemActivity.newIntent(this, true))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        super.onStop()

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun onCardSelected(obj: OnCardClickedEvent) {
        startActivity(Intent(applicationContext, DetailItemActivity::class.java))
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, UserItemsActivity::class.java)
        }
    }
}
