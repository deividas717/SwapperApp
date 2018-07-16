package app.swapper.com.swapper.ui.fragment

import android.databinding.DataBindingUtil
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.swapper.com.swapper.LocationData
import app.swapper.com.swapper.R
import app.swapper.com.swapper.SwaggerApp
import app.swapper.com.swapper.TradeType
import app.swapper.com.swapper.databinding.FragmentSwipeBinding
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.ui.viewmodel.SwipeViewModel
import kotlinx.android.synthetic.main.fragment_swipe.*

/**
 * A simple [Fragment] subclass.
 */
class SwipeFragment : Fragment() {

    private lateinit var swipeViewModel: SwipeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentSwipeBinding>(inflater, R.layout.fragment_swipe, container, false)

        val application = activity?.application as SwaggerApp

        swipeViewModel = SwipeViewModel(application.getUser(), application.getRetrofit())
        binding.swipeViewModel = swipeViewModel

//        swipeView = binding.root.findViewById(R.id.swipeView);
//        swipeView.getBuilder<SwipeView, SwipeViewBuilder<SwipeView>>()
//                .setDisplayViewCount(3)
//                .setSwipeDecor(SwipeDecor().setPaddingTop(20).setRelativeScale(0.01f))
//
//        swipeViewModel.data.observe(this, android.arch.lifecycle.Observer { handleData(it) })
//
//        swipeView.addItemRemoveListener { count ->
//            (activity as MainActivity).resetAllSelectableStates()
//            swipeViewModel.askForMoreCards(count)
//        }

        return binding.root
    }

    private fun handleData(data: List<Item>?) {
        data?.forEach {
           // swipeView.addView(CardPresenter(it))
        }
    }

    fun getActiveCardId() : Long {
        return swipeViewModel.getActiveCardId()
    }

    override fun onResume() {
        super.onResume()

        LocationData.location?.let {
            swipeViewModel.changeLocation(it)
        }
    }

    fun onLocationChanged(location: Location) {
        swipeViewModel.changeLocation(location)
    }

    fun changeType(type: TradeType) {
        swipeViewModel.tradeType = type
    }
}