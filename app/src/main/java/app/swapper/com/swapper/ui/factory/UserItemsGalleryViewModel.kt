package app.swapper.com.swapper.ui.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.ui.viewmodel.UserProfileItemViewModel

/**
 * Created by Deividas on 2018-05-01.
 */
class UserItemsGalleryViewModel(private val service: ApiService?, private val user: User?): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserProfileItemViewModel(service, user) as T
    }
}