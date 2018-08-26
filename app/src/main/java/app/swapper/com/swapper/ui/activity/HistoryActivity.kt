package app.swapper.com.swapper.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import app.swapper.com.swapper.R
import app.swapper.com.swapper.SwapperApp
import app.swapper.com.swapper.ui.viewmodel.factory.HistoryViewModelFactory
import app.swapper.com.swapper.ui.viewmodel.HistoryViewModel
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import app.swapper.com.swapper.adapter.HistoryItemsAdapter
import app.swapper.com.swapper.events.OnCardClickedEvent
import kotlinx.android.synthetic.main.activity_history.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.support.v7.app.AlertDialog
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.events.LocationChangeEvent
import app.swapper.com.swapper.events.OnItemsDelete
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.storage.SharedPreferencesManager
import dagger.android.AndroidInjection
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyAdapter: HistoryItemsAdapter
    private lateinit var deleteIcon: MenuItem

    @Inject
    lateinit var prefs: SharedPreferencesManager

    @Inject
    lateinit var apiService: ApiService

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, HistoryViewModelFactory(apiService, prefs.getUser()?.email)).get(HistoryViewModel::class.java)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        historyRecyclerView.layoutManager = linearLayoutManager

        val location = Location("PhoneLocation")
        val lat = intent.getDoubleExtra(LAT, -1.0)
        val lng = intent.getDoubleExtra(LNG, -1.0)

        historyAdapter = HistoryItemsAdapter()
        if (lat != -1.0 && lng != -1.0) {
            location.latitude = lat
            location.longitude = lng
            historyAdapter.location = location
        }

        viewModel.userList?.observe(this, android.arch.lifecycle.Observer { historyAdapter.submitList(it) })

        historyRecyclerView.adapter = historyAdapter
    }

    @Subscribe
    fun onCardClick(obj: OnCardClickedEvent) {
        startActivity(DetailItemActivity.createNewIntent(applicationContext, obj.itemId))
    }

    @Subscribe
    fun onItemsSelectedToDelete(obj: OnItemsDelete) {
        deleteIcon.isVisible = !obj.isEmpty
    }

    @Subscribe
    fun onLocationChangeEvent(obj: LocationChangeEvent) {
        historyAdapter.location = obj.location
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.user_history_menu, menu)
        deleteIcon = menu.getItem(0)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> {
                if (historyAdapter.isDeleteMode) {
                    showDeleteConfirmationDialog()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteConfirmationDialog() {
        val userId = prefs.getUser()?.userId
        if (userId != null) {
            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val result = apiService.deleteUserHistoryItems(userId, historyAdapter.selectedItems)
                        result.enqueue(object : retrofit2.Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                                when (response?.isSuccessful) {
                                    true -> {
                                        historyAdapter.removeDataFromList()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                                dialog.dismiss()
                            }
                        })
                    }

                    DialogInterface.BUTTON_NEGATIVE -> {

                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure you want to delete these items from history?").setMessage("They may still be visible in the card stack").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()
        }
    }

    companion object {
        const val LAT = "lat"
        const val LNG = "lng"

        fun createNewIntent(context: Context?, lat: Double?, lng: Double?): Intent {
            return Intent(context, HistoryActivity::class.java).putExtra(LAT, lat).putExtra(LNG, lng)
        }
    }
}
