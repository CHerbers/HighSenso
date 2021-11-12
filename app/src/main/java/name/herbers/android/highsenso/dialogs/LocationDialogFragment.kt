package name.herbers.android.highsenso.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.databinding.DialogLocationBinding
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class LocationDialogFragment(
    private val preferences: SharedPreferences,
    private val sharedViewModel: SharedViewModel
) : DialogFragment() {
    private lateinit var binding: DialogLocationBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_location, null, false
            )
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)

            builder.setTitle(R.string.location_dialog_title)

            setButtonStyles()
            setButtonListeners()

            val dialog = builder.create()
            dialog
        }
    }

    private fun setButtonStyles() {
        val buttonList: MutableList<Button> = mutableListOf()

        if (preferences.getBoolean(getString(R.string.location_option_home_key), false))
            buttonList.add(binding.locationDialogOptionHomeButton)
        if (preferences.getBoolean(getString(R.string.location_option_work_key), false))
            buttonList.add(binding.locationDialogOptionWorkButton)
        if (preferences.getBoolean(getString(R.string.location_option_outside_key), false))
            buttonList.add(binding.locationDialogOptionOutsideButton)

        buttonList.forEach { button ->
            button.backgroundTintList = context?.let {
                ContextCompat.getColorStateList(
                    it,
                    R.color.location_dialog_button_done_color
                )
            }
        }
    }

    private fun setButtonListeners() {
        val homeButton = binding.locationDialogOptionHomeButton
        val workButton = binding.locationDialogOptionWorkButton
        val outsideButton = binding.locationDialogOptionOutsideButton
        val elseButton = binding.locationDialogOptionElseButton
        var currentLocation = sharedViewModel.currentLocation

        val listener = View.OnClickListener { view ->
            when (view) {
                homeButton -> currentLocation =
                    getString(R.string.location_dialog_option_home)
                workButton -> currentLocation =
                    getString(R.string.location_dialog_option_work)
                outsideButton -> currentLocation =
                    getString(R.string.location_dialog_option_outside)
                elseButton -> currentLocation =
                    getString(R.string.location_dialog_option_else)
            }
            Timber.i("Current location is: $currentLocation")
            preferences.edit().putBoolean(getString(R.string.location_option_work_key), true)
                .apply()
            sharedViewModel.dialogGetsDismissed()
            dismiss()
        }
        homeButton.setOnClickListener(listener)
        workButton.setOnClickListener(listener)
        outsideButton.setOnClickListener(listener)
        elseButton.setOnClickListener(listener)
    }
}