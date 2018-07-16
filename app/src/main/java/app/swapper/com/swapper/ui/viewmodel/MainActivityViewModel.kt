package app.swapper.com.swapper.ui.viewmodel

import android.arch.lifecycle.ViewModel
import app.swapper.com.swapper.networking.ApiService

class MainActivityViewModel(private val apiService: ApiService?, private val email: String?): ViewModel() {

}