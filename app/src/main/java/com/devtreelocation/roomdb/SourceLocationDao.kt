package com.devtreelocation.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*
import com.devtreelocation.model.SourceLocation

// UserDao contains the methods used for accessing the database, including queries.
@Dao
interface SourceLocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // <- Annotate the 'addSourceLocation' function below. Set the onConflict strategy to IGNORE so if exactly the same source location exists, it will just ignore it.
    suspend fun addSourceLocation(sourceLocation: SourceLocation)

    @Update
    suspend fun updateSourceLocation(sourceLocation: SourceLocation)

    @Delete
    suspend fun deleteSourceLocation(sourceLocation: SourceLocation)

    @Query("SELECT * from source_location_table ORDER BY id DESC") // <- Add a query to fetch all source location (in source_location_table) in ascending order by their IDs.
    fun readAllData(): LiveData<List<SourceLocation>> // <- This means function return type is List. Specifically, a List of source location.

}