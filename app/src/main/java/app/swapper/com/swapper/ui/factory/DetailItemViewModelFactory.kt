package app.swapper.com.swapper.ui.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.ui.viewmodel.CreateNewItemViewModel
import app.swapper.com.swapper.ui.viewmodel.DetailItemViewModel

class DetailItemViewModelFactory(private val apiService: ApiService?) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailItemViewModel(apiService) as T
    }
}