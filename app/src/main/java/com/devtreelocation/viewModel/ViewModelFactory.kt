package com.devtreelocation.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddSourceLocationViewModel::class.java)) {
            AddSourceLocationViewModel(context) as T
        } else if (modelClass.isAssignableFrom(SourceLocationViewModel::class.java)) {
            SourceLocationViewModel(context) as T
        } else {
            SourceLocationMapViewModel(context) as T
        }
    }
}