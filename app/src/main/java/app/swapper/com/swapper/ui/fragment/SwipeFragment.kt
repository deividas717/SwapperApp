package app.swapper.com.swapper.ui.fragment

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import app.swapper.com.swapper.*
import app.swapper.com.swapper.databinding.FragmentSwipeBinding
import app.swapper.com.swapper.ui.activity.DetailItemActivity
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.events.LocationChangeEvent
import app.swapper.com.swapper.networking.GlideLoader
import app.swapper.com.swapper.ui.SwipeView
import app.swapper.com.swapper.ui.viewmodel.SwipeViewModel
import app.swapper.com.swapper.utils.Constants
import com.bumptech.glide.Glide
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeViewBuilder
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.NonReusable
import com.mindorks.placeholderview.annotations.Resolve
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class SwipeFragment : Fragment() {

    private lateinit var swipeView: SwipeView
    private lateinit var swipeViewModel: SwipeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentSwipeBinding>(inflater, R.layout.fragment_swipe, container, false)

        val application = activity?.application as SwaggerApp

        swipeViewModel = SwipeViewModel(application.getUser(), application.getRetrofit())
        binding.swipeViewModel = swipeViewModel

        swipeView = binding.root.findViewById(R.id.swipeView);
        swipeView.getBuilder<SwipeView, SwipeViewBuilder<SwipeView>>()
                .setDisplayViewCount(3)
                .setSwipeDecor(SwipeDecor().setPaddingTop(20).setRelativeScale(0.01f))

        swipeViewModel.data.observe(this, android.arch.lifecycle.Observer { handleData(it) })

        swipeView.addItemRemoveListener { count ->
            swipeViewModel.changeCurrentItemIndex(true)
            if (count < 5) swipeViewModel.getMoreCards()
        }

        return binding.root
    }

    private fun handleData(data: List<Item>?) {
        data?.forEach {
            swipeView.addView(CardPresenter(it))
        }
        swipeViewModel.increaseIndex()
    }

    fun getActiveCardId() : Long {
        return swipeViewModel.getActiveCardId()
    }

    override fun onResume() {
        super.onResume()

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        LocationData.location?.let {
            swipeViewModel.changeLocation(it)
        }
    }

    override fun onStop() {
        super.onStop()

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun onLocationChanged(obj: LocationChangeEvent) {
        swipeViewModel.changeLocation(obj.location)
    }

    @NonReusable
    @Layout(R.layout.card_layout)
    inner class CardPresenter(private var item: Item) {

        @com.mindorks.placeholderview.annotations.View(R.id.card)
        private var cardView: CardView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.itemImg)
        private lateinit var itemImg: ImageView
        @com.mindorks.placeholderview.annotations.View(R.id.itemName)
        private var itemName: TextView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.itemDescription)
        private var itemDescription: TextView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.profileImg)
        private lateinit var profileImg: CircleImageView

        @Resolve
        private fun onResolved() {
            Glide.with(this@SwipeFragment).load(item.user.img).into(profileImg)
            item.images?.let {
                if (it.isNotEmpty()) {
                    val url = Constants.serverAddress + "api/image" + File.separator + item.images?.get(0)
                    GlideLoader.loadFromApi(activity, itemImg, url)
                }
            }

            itemName?.text = item.title
            itemDescription?.text = item.description

            cardView?.setOnClickListener {
                val intent = Intent(activity, DetailItemActivity::class.java)
                intent.putExtra(DetailItemActivity.itemId, item.id)
                startActivity(intent);
            }
        }
    }
}