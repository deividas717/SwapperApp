package app.swapper.com.swapper.dto

/**
 * Created by Deividas on 2018-04-07.
 */
data class Item(val id: Long,
                val title : String,
                val description : String,
                val images : MutableList<String>?,
                val lat : Double,
                val lng : Double,
                val user : User) {
    constructor(title : String,
                description : String,
                images : MutableList<String>?,
                lat : Double,
                lng : Double,
                user : User)
            : this(-1, title, description, images, lat, lng, user)
}