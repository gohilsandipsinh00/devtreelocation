package com.devtreelocation.utils

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import com.google.android.libraries.places.api.model.Place
import java.util.*

/** Helper class for selecting [Field] values.  */
class FieldSelector(
    outputView: TextView,
    savedState: Bundle?,
    validFields: List<Place.Field> = listOf(*Place.Field.values())
) {

    private val fieldStates: MutableMap<Place.Field, State>
    private val outputView: TextView

    /**
     * Shows dialog to allow user to select [Field] values they want.
     */
    fun showDialog(context: Context?) {
        val listView = ListView(context)
        val adapter = PlaceFieldArrayAdapter(context, fieldStates.values.toList())
        listView.adapter = adapter
        listView.onItemClickListener = adapter
        AlertDialog.Builder(context!!)
            .setTitle("Select Place Fields")
            .setPositiveButton(
                "Done"
            ) { _, _ -> outputView.text = selectedString }
            .setView(listView)
            .show()
    }

    /**
     * Returns all [Field] that are selectable.
     */
    val allFields: List<Place.Field>
        get() = ArrayList(fieldStates.keys)

    /**
     * Returns all [Field] values the user selected.
     */
    val selectedFields: List<Place.Field>
        get() {
            val selectedList: MutableList<Place.Field> = ArrayList()
            for ((key, value) in fieldStates) {
                if (value.checked) {
                    selectedList.add(key)
                }
            }
            return selectedList
        }

    /**
     * Returns a String representation of all selected [Field] values. See [ ][.getSelectedFields].
     */
    val selectedString: String
        get() {
            val builder = StringBuilder()
            for (field in selectedFields) {
                builder.append(field).append("\n")
            }
            return builder.toString()
        }

    private fun restoreState(selectedFields: List<Int>) {
        for (serializedField in selectedFields) {
            val field = Place.Field.values()[serializedField]
            val state = fieldStates[field]
            if (state != null) {
                state.checked = true
            }
        }
    }
    //////////////////////////
    // Helper methods below //
    //////////////////////////
    /**
     * Holds selection state for a place field.
     */
    class State(val field: Place.Field) {
        var checked = false
    }

    private class PlaceFieldArrayAdapter(
        context: Context?,
        states: List<State>?
    ) : ArrayAdapter<State>(
        context!!,
        android.R.layout.simple_list_item_multiple_choice,
        states!!.toMutableList()
    ), OnItemClickListener {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val state = getItem(position)
            updateView(view, state)
            return view
        }

        override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            val state = getItem(position)
            state!!.checked = !state.checked
            updateView(view, state)
        }

        companion object {
            private fun updateView(view: View, state: State?) {
                if (view is CheckedTextView && state != null) {
                    view.text = state.field.toString()
                    view.isChecked = state.checked
                }
            }
        }
    }

    companion object {
        private const val SELECTED_PLACE_FIELDS_KEY = "selected_place_fields"
    }

    init {
        fieldStates = HashMap()
        for (field in validFields) {
            fieldStates[field] = State(field)
        }
        if (savedState != null) {
            val selectedFields: List<Int>? =
                savedState.getIntegerArrayList(SELECTED_PLACE_FIELDS_KEY)
            selectedFields?.let { restoreState(it) }
            outputView.text = selectedString
        }
        outputView.setOnClickListener { v: View ->
            if (v.isEnabled) {
                showDialog(v.context)
            }
        }

        this.outputView = outputView
    }
}