package app.swapper.com.swapper.ui.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.ui.viewmodel.CreateNewItemViewModel

/**
 * Created by Deividas on 2018-04-28.
 */
class CreateNewItemViewModelFactory(private val apiService: ApiService?) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateNewItemViewModel(apiService) as T
    }
}