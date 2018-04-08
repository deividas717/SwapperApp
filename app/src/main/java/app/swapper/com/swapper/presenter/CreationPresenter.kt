package app.swapper.com.swapper.presenter

import app.swapper.com.swapper.dto.Item
import java.io.File

/**
 * Created by Deividas on 2018-04-07.
 */
interface CreationPresenter {
    fun sendItemDataToServer(item: Item, files : List<File>)
}