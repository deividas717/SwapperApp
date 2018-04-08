package app.swapper.com.swapper.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import app.swapper.com.swapper.Constants
import app.swapper.com.swapper.R
import app.swapper.com.swapper.SwaggerApp
import app.swapper.com.swapper.SwipeView
import app.swapper.com.swapper.activity.DetailItemActivity
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.model.CardPresenterImpl
import app.swapper.com.swapper.presenter.CardsPresenter
import app.swapper.com.swapper.view.CardsView
import com.bumptech.glide.Glide
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipeViewBuilder
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.NonReusable
import com.mindorks.placeholderview.annotations.Resolve
import java.io.File

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

        val application = activity?.application as SwaggerApp
        val user = application.getUser()

        cardsPresenter = CardPresenterImpl(this)
        cardsPresenter.getMoreCards(user, index)
        swipeView.addItemRemoveListener { count ->
            cardsPresenter.markCardAsAlreadySeen(user, dataList[currentItem].id)
            currentItem++
            if (count < 5) cardsPresenter.getMoreCards(user, index)
        }

        dataList = mutableListOf()

        return view
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

        @com.mindorks.placeholderview.annotations.View(R.id.card)
        private var cardView: CardView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.itemImg)
        private var itemImg: ImageView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.itemName)
        private var itemName: TextView? = null
        @com.mindorks.placeholderview.annotations.View(R.id.itemDescription)
        private var itemDescription: TextView? = null

        @Resolve
        private fun onResolved() {
            item.images?.let {
                itemImg?.let {
                    if (item.images!!.isNotEmpty()) {
                        val url = Constants.serverAddress + "image" + File.separator + item.images?.get(0)
                        Glide.with(activity?.applicationContext!!)
                                .load(url)
                                .into(it)
                    } else {
                        Glide.with(activity?.applicationContext!!).load("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6c/No_image_3x4.svg/1024px-No_image_3x4.svg.png").into(it)
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