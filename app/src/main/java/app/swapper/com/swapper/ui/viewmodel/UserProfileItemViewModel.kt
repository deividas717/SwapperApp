package app.swapper.com.swapper.ui.viewmodel

import app.swapper.com.swapper.adapter.UserItemsGridAdapter
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService

/**
 * Created by Deividas on 2018-05-01.
 */
class UserProfileItemViewModel(service: ApiService?, user: User?) : RecyclerViewViewModel<UserItemsGridAdapter>(service, user) {
    private var adapter: UserItemsGridAdapter = UserItemsGridAdapter()

    override fun getAdapter(): UserItemsGridAdapter {
        return adapter
    }
}