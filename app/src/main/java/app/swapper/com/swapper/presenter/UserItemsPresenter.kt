package app.swapper.com.swapper.presenter

/**
 * Created by Deividas on 2018-04-07.
 */
interface UserItemsPresenter {
    fun askServerForUserItems(email: String)
    fun sendItemExchangeRequest(itemId: Long, ids: List<Long>)
}