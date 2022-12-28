package com.devtreelocation.repository


import androidx.lifecycle.LiveData
import com.devtreelocation.model.SourceLocation
import com.devtreelocation.roomdb.SourceLocationDao

// SourceLocation Repository abstracts access to multiple data sources. However this is not the part of the Architecture Component libraries.

class SourceLocationRepository(private val sourceLocationDao: SourceLocationDao) {

    fun readAllData(): LiveData<List<SourceLocation>> {
        return sourceLocationDao.readAllData()
    }

    suspend fun addSourceLocation(sourceLocation: SourceLocation) {
        sourceLocationDao.addSourceLocation(sourceLocation)
    }

    suspend fun updateSourceLocation(sourceLocation: SourceLocation) {
        sourceLocationDao.updateSourceLocation(sourceLocation)
    }

    suspend fun deleteSourceLocation(sourceLocation: SourceLocation) {
        sourceLocationDao.deleteSourceLocation(sourceLocation)
    }

}