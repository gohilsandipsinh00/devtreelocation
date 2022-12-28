package com.devtreelocation.viewModel

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtreelocation.model.SourceLocation
import com.devtreelocation.repository.SourceLocationRepository
import com.devtreelocation.roomdb.SourceLocationDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SourceLocationViewModel(context: Context) : ViewModel() {

    var isSort = 0 // 0-default , 1-asc , 2-desc

    var readAllData: LiveData<List<SourceLocation>>

    var showSortingDialog: MutableLiveData<Boolean> = MutableLiveData(false)
    var addNewLocation: MutableLiveData<Boolean> = MutableLiveData(false)
    var showDirection: MutableLiveData<Boolean> = MutableLiveData(false)

    private val repository: SourceLocationRepository

    init {
        val userDao = SourceLocationDatabase.getDatabase(context).sourceLocationDao()
        repository = SourceLocationRepository(userDao)
        isSort = 0
        readAllData = repository.readAllData()
    }

    fun deleteSourceLocation(sourceLocation: SourceLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSourceLocation(sourceLocation)
        }
    }

    fun onClickSort(view: View) {
        showSortingDialog.value = true
    }

    fun onClickAddNewLocation(view: View) {
        addNewLocation.value = true
    }

    fun onClickDirection(view: View) {
        showDirection.value = true
    }

}