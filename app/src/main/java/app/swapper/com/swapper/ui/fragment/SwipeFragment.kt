package app.swapper.com.swapper.ui.fragment

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import app.swapper.com.swapper.*
import app.swapper.com.swapper.databinding.FragmentSwipeBinding
import app.swapper.com.swapper.ui.activity.DetailItemActivity
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.networking.GlideLoader
import app.swapper.com.swapper.ui.viewmodel.SwipeViewModel
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeViewBuilder
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.NonReusable
import com.mindorks.placeholderview.annotations.Resolve
import de.hdodenhof.circleimageview.CircleImageView
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

        swipeViewModel = SwipeViewModel(application.getUser())
        binding.swipeViewModel = swipeViewModel

        swipeView = binding.root.findViewById(R.id.swipeView);
        swipeView.getBuilder<SwipeView, SwipeViewBuilder<SwipeView>>()
                .setDisplayViewCount(3)
                .setSwipeDecor(SwipeDecor().setPaddingTop(20).setRelativeScale(0.01f))

        swipeViewModel.data.observe(this, android.arch.lifecycle.Observer { handleData(it) })

        swipeViewModel.getMoreCards()
        swipeView.addItemRemoveListener { count ->
            swipeViewModel.changeCurrentItemIndex(true)
            if (count < 5) swipeViewModel.getMoreCards()
        }

        return binding.root
    }

    private fun handleData(data: List<Item>?) {
        Log.d("ASDAGSUDSDD", "handleData")
        data?.forEach {
            swipeView.addView(CardPresenter(it))
        }
        swipeViewModel.increaseIndex()
            //                swipeView.addView(CardPresenter(it))
//            }
//        data?.let {
//            if (it.failure) {
//                return
//            }
//            it.data.forEach {
//                swipeView.addView(CardPresenter(it))
//            }
//            swipeViewModel.increaseIndex()
//        }
    }

    fun getActiveCardId() : Long {
        return swipeViewModel.getActiveCardId()
    }

    @NonReusable
    @Layout(R.layout.card_layout)
    inner class CardPresenter(private var item: Item) {

        @com.mindorks.placeholderview.annotations.View(R.id.card)
        private var cardView: CardView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.itemImg)
        private var itemImg: ImageView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.itemName)
        private var itemName: TextView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.itemDescription)
        private var itemDescription: TextView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.profileImg)
        private var profileImg: CircleImageView? = null

        @Resolve
        private fun onResolved() {
            item.images?.let {
                itemImg?.let {
                    if (item.images!!.isNotEmpty()) {
                        val url = Constants.serverAddress + "api/image" + File.separator + item.images?.get(0)
                        GlideLoader.load(activity, it, url)
//                        Glide.with(activity?.applicationContext!!)
//                                .load(url)
//                                .into(it)
                    } else {
                        //Glide.with(activity?.applicationContext!!).load("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6c/No_image_3x4.svg/1024px-No_image_3x4.svg.png").into(it)
                    }
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