package com.devtreelocation.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.devtreelocation.R
import com.devtreelocation.databinding.ActivitySourceLocationBinding
import com.devtreelocation.listener.SourceLocationListener
import com.devtreelocation.model.SourceLocation
import com.devtreelocation.utils.KEY_IS_ID
import com.devtreelocation.utils.KEY_IS_SHORT
import com.devtreelocation.utils.KEY_IS_UPDATE
import com.devtreelocation.viewModel.SourceLocationViewModel
import com.devtreelocation.viewModel.ViewModelFactory

class SourceLocationActivity : AppCompatActivity(), SourceLocationListener {

    private lateinit var binding: ActivitySourceLocationBinding
    private lateinit var viewModel: SourceLocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySourceLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this@SourceLocationActivity,
            ViewModelFactory(this@SourceLocationActivity)
        )[SourceLocationViewModel::class.java]

        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = this@SourceLocationActivity
            adapter = SourceLocationAdapter(this@SourceLocationActivity)
        }

        initSortingDialog()
        setObservers()

    }

    lateinit var dialog: Dialog

    private fun initSortingDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_sorting)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnSort = dialog.findViewById<Button>(R.id.btnSort)
        val rgSortingGroup = dialog.findViewById<RadioGroup>(R.id.rgSortingGroup)
        val rbSortDesc = dialog.findViewById<RadioButton>(R.id.rbSortDesc)

        btnSort.setOnClickListener {
            if (rgSortingGroup!!.checkedRadioButtonId == rbSortDesc.id) {
                viewModel.isSort = 2
                binding.adapter!!.updateData(
                    viewModel.readAllData.value!!.sortedWith(
                        compareByDescending(
                            SourceLocation::locationName
                        )
                    )
                )
            } else {
                viewModel.isSort = 1
                binding.adapter!!.updateData(
                    viewModel.readAllData.value!!.sortedWith(
                        compareBy(
                            SourceLocation::locationName
                        )
                    )
                )
            }
            dialog.dismiss()
        }

    }

    private fun setObservers() {
        viewModel.showSortingDialog.observe(this) {
            if (it) {
                showSortingDialog()
            } else {
                dismissSortingDialog()
            }
        }

        viewModel.addNewLocation.observe(this) {
            if (it) {
                startActivity(
                    Intent(
                        this@SourceLocationActivity,
                        AddSourceLocationActivity::class.java
                    )
                )
                viewModel.addNewLocation.value = false
            }
        }

        viewModel.showDirection.observe(this) {
            if (it) {

                val intent =
                    Intent(this@SourceLocationActivity, SourceLocationMapActivity::class.java)
                intent.putExtra(KEY_IS_SHORT, viewModel.isSort)
                startActivity(intent)

                viewModel.showDirection.value = false
            }
        }
    }

    private fun showSortingDialog() {
        dialog.show()
    }

    private fun dismissSortingDialog() {
        dialog.dismiss()
    }

    override fun onUpdateClicked(sourceLocation: SourceLocation) {
        val intent = Intent(this@SourceLocationActivity, AddSourceLocationActivity::class.java)
        intent.putExtra(KEY_IS_UPDATE, true)
        intent.putExtra(KEY_IS_ID, sourceLocation.id)
        startActivity(intent)
    }

    override fun onDeleteClicked(sourceLocation: SourceLocation) {
        val builder = AlertDialog.Builder(this)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setTitle(getString(R.string.str_alert))
        builder.setMessage(getString(R.string.str_alert_delete))

        builder.setPositiveButton(getString(R.string.str_yes)) { _, _ ->
            viewModel.deleteSourceLocation(sourceLocation)
        }
        builder.setNeutralButton(getString(R.string.str_cancel)) { d, _ ->
            d.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}