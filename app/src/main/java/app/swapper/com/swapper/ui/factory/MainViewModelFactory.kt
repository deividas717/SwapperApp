package app.swapper.com.swapper.ui.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import app.swapper.com.swapper.dto.User
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.ui.viewmodel.MainViewModel

/**
 * Created by Deividas on 2018-05-01.
 */
class MainViewModelFactory(private val apiService: ApiService?, private val user: User?) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(apiService, user) as T
    }
}