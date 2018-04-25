package app.swapper.com.swapper.model

import android.util.Log
import app.swapper.com.swapper.dto.AccessToken
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.RetrofitSingleton
import app.swapper.com.swapper.presenter.CardsPresenter
import app.swapper.com.swapper.view.CardsView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Deividas on 2018-04-07.
 */
class CardPresenterImpl(var cardsView : CardsView) : CardsPresenter {

    override fun getMoreCards(accesToken : AccessToken, user: User?, index : Int) {
        user?.let {
            val result = RetrofitSingleton.service.getNearestItems("deividas@gmail.com", 54.7, 23.5, index)
            result.enqueue(object : Callback<List<Item>> {
                override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                    response.let {
                        val list = response?.body()
                        list?.let {
                            cardsView.cardsArrived(list)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {

                }
            })
        }
    }

    // todo make request to server and mark item as seen
    override fun markCardAsAlreadySeen(user: User?, itemId: Long) {
        user?.let {

        }
    }
}