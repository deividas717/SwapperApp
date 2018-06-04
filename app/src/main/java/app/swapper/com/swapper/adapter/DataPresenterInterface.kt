package app.swapper.com.swapper.adapter

import app.swapper.com.swapper.dto.Item

/**
 * Created by Deividas on 2018-05-01.
 */
interface DataPresenterInterface {
    fun setDataList(data: List<Item>)
}