package com.devtreelocation.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtreelocation.model.SourceLocation
import com.devtreelocation.repository.SourceLocationRepository
import com.devtreelocation.roomdb.SourceLocationDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddSourceLocationViewModel(context: Context) : ViewModel() {

    private val repository: SourceLocationRepository

    init {
        val userDao = SourceLocationDatabase.getDatabase(context).sourceLocationDao()
        repository = SourceLocationRepository(userDao)
    }

    fun addSourceLocation(sourceLocation: SourceLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSourceLocation(sourceLocation)
        }
    }

    fun updateSourceLocation(sourceLocation: SourceLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSourceLocation(sourceLocation)
        }
    }


}