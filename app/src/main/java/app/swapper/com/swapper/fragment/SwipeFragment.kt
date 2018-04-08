package app.swapper.com.swapper.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import app.swapper.com.swapper.R
import app.swapper.com.swapper.SwaggerApp
import app.swapper.com.swapper.SwipeView
import app.swapper.com.swapper.activity.MainActivity
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.model.CardPresenterImpl
import app.swapper.com.swapper.presenter.CardsPresenter
import app.swapper.com.swapper.storage.SharedPreferencesManager
import app.swapper.com.swapper.view.CardsView
import com.bumptech.glide.Glide
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeViewBuilder
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.NonReusable
import com.mindorks.placeholderview.annotations.Resolve

/**
 * A simple [Fragment] subclass.
 */
class SwipeFragment : Fragment(), CardsView {

    internal lateinit var cardsContainer: SwipeView
    private var index: Int = 0
    private var currentItem = 0
    private lateinit var swipeView: SwipeView
    private lateinit var cardsPresenter: CardsPresenter

    private lateinit var dataList : MutableList<Item>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_swipe, container, false)
        swipeView = view.findViewById(R.id.swipeView);
        swipeView.getBuilder<SwipeView, SwipeViewBuilder<SwipeView>>()
                .setDisplayViewCount(3)
                .setSwipeDecor(SwipeDecor().setPaddingTop(20).setRelativeScale(0.01f))

        val user = (activity as MainActivity).getUser()

        cardsPresenter = CardPresenterImpl(this)
        cardsPresenter.performNetworkRequest(user, index)
        swipeView.addItemRemoveListener { count ->
            currentItem++
            if (count < 5) cardsPresenter.performNetworkRequest(user, index)
        }

        dataList = mutableListOf()

        return view;
    }

    override fun cardsArrived(list: List<Item>) {
        list.forEach {
            swipeView.addView(CardPresenter(it))
        }
        dataList.addAll(list)
        index += 5;
    }

    fun getActiveCardId() : Long {
        if (dataList.size <= currentItem) return -1L
        return dataList[currentItem].id
    }

    @NonReusable
    @Layout(R.layout.card_layout)
    inner class CardPresenter(private var item: Item) {
        @com.mindorks.placeholderview.annotations.View(R.id.itemImg)
        private var itemImg: ImageView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.itemName)
        private var itemName: TextView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.itemDescription)
        private var itemDescription: TextView? = null

        @Resolve
        private fun onResolved() {
            if (item.images?.isNotEmpty()!!) {
                itemImg?.let { Glide.with(context!!.applicationContext).load("http://192.168.1.103:8080/api/image/" + item.images?.get(0)).into(it) }
            }
            itemName?.text = item.title
            itemDescription?.text = item.description
        }
    }
}