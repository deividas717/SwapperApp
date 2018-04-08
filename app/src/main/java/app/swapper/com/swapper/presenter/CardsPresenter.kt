package app.swapper.com.swapper.presenter

import app.swapper.com.swapper.dto.User

/**
 * Created by Deividas on 2018-04-07.
 */
interface CardsPresenter {
    fun getMoreCards(user: User?, index : Int)
    fun markCardAsAlreadySeen(user: User?, itemId : Long)
}