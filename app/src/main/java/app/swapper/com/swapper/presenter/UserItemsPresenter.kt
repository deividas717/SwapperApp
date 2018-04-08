package app.swapper.com.swapper.presenter

import android.location.Location
import app.swapper.com.swapper.dto.User

/**
 * Created by Deividas on 2018-04-07.
 */
interface UserItemsPresenter {
    fun askServerForUserItems(user: User?)
    fun sendItemExchangeRequest(itemId: Long, ids: List<Long>)
}