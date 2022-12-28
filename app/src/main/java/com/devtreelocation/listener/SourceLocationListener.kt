package com.devtreelocation.listener

import com.devtreelocation.model.SourceLocation

interface SourceLocationListener {
    fun onUpdateClicked(sourceLocation: SourceLocation)
    fun onDeleteClicked(sourceLocation: SourceLocation)
}
