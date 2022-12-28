package com.devtreelocation.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devtreelocation.databinding.ViewSourceLocationListItemBinding
import com.devtreelocation.listener.SourceLocationListener
import com.devtreelocation.model.SourceLocation

class SourceLocationAdapter(private val sourceLocationListener: SourceLocationListener) :
    RecyclerView.Adapter<SourceLocationAdapter.ViewHolder>() {

    private var sourceLocationList: List<SourceLocation> = ArrayList()
    private lateinit var binding: ViewSourceLocationListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ViewSourceLocationListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sort = sourceLocationList[position]
        holder.bind(sort, sourceLocationListener)
    }

    override fun getItemCount(): Int = sourceLocationList.size

    class ViewHolder(private val itemRowBinding: ViewSourceLocationListItemBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {

        fun bind(sourceLocation: SourceLocation, sourceLocationListener: SourceLocationListener) {
            itemRowBinding.datamodel = sourceLocation
            itemRowBinding.listener = sourceLocationListener
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(sourceLocationList: List<SourceLocation>) {
        this.sourceLocationList = sourceLocationList
        notifyDataSetChanged()
    }

}