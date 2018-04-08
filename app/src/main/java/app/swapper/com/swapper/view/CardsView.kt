package app.swapper.com.swapper.view

import app.swapper.com.swapper.dto.Item

/**
 * Created by Deividas on 2018-04-07.
 */
interface CardsView {
    fun cardsArrived(list : List<Item>)
}