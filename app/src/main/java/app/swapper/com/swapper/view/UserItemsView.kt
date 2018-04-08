package app.swapper.com.swapper.view

import app.swapper.com.swapper.dto.Item

/**
 * Created by Deividas on 2018-04-07.
 */
interface UserItemsView {
    fun itemsLoaded(list: List<Item>)
}