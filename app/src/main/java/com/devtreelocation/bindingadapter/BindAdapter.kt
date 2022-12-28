package com.tmsorting.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devtreelocation.model.SourceLocation
import com.devtreelocation.view.SourceLocationAdapter

class BindAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("setAdapter")
        fun setAdapter(
            recyclerView: RecyclerView,
            adapter: SourceLocationAdapter
        ) {
            adapter.let {
                recyclerView.adapter = it
            }
        }

        @JvmStatic
        @BindingAdapter("submitList")
        fun submitList(recyclerView: RecyclerView, list: List<SourceLocation>?) {
            val adapter = recyclerView.adapter as SourceLocationAdapter?
            adapter?.updateData(list ?: listOf())
        }
    }


}