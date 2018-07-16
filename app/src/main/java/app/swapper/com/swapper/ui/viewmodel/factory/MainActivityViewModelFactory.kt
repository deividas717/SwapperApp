package app.swapper.com.swapper.ui.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.ui.viewmodel.MainActivityViewModel

class MainActivityViewModelFactory(private val apiService: ApiService?, private val email: String?) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(apiService, email) as T
    }
}