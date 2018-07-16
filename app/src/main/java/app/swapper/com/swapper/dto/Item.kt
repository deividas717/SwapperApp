package app.swapper.com.swapper.dto

import app.swapper.com.swapper.TradeType

/**
 * Created by Deividas on 2018-04-07.
 */
data class Item(val id: Long = -1L,
                val title : String,
                val description : String,
                val images : MutableList<String>?,
                val lat : Double,
                val lng : Double,
                val user : User,
                val price: Double,
                val tradeType: TradeType,
                val candidatesCount: Int = 0) {
    constructor(title : String,
                description : String,
                images : MutableList<String>?,
                lat : Double,
                lng : Double,
                tradeType: TradeType,
                user : User)
            : this(-1, title, description, images, lat, lng, user, (-1).toDouble(), tradeType)
}