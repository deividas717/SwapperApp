package app.swapper.com.swapper.ui.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import app.swapper.com.swapper.networking.ApiService
import app.swapper.com.swapper.ui.viewmodel.HistoryViewModel

class HistoryViewModelFactory(private val apiService: ApiService?, private val email: String?) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HistoryViewModel(apiService, email) as T
    }
}