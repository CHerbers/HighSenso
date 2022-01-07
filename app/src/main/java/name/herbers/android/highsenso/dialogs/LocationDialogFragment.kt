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
import name.herbers.android.highsenso.data.AnswerSheet
import name.herbers.android.highsenso.databinding.DialogLocationBinding
import timber.log.Timber

/**
 * This [DialogFragment] asks the user for their location.
 * After answering it with one of the location buttons, the current location is stored until it is
 * send to the webserver inside of an [AnswerSheet].
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

            //set up buttons
            setButtonBackgroundTints()
            setButtonListeners()

            val dialog = builder.create()
            dialog
        }
    }

    /**
     * This method changes the background tint of a Button if its corresponding preferences key is
     * true. The tint is changed to [R.color.location_dialog_button_done_color] to show that a
     * questioning was already done on this location.
     * */
    private fun setButtonBackgroundTints() {
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

    /**
     * This method sets an OnClickListener to all of the four [Button]s shown onScreen.
     * When a Button is clicked, the current location of the personalData is changed to the
     * corresponding value of the Button.
     * The [DialogFragment] gets dismissed after.
     * */
    private fun setButtonListeners() {
        val homeButton = binding.locationDialogOptionHomeButton
        val workButton = binding.locationDialogOptionWorkButton
        val outsideButton = binding.locationDialogOptionOutsideButton
        val elseButton = binding.locationDialogOptionElseButton
        var currentLocation = sharedViewModel.currentLocation

        val listener = View.OnClickListener { view ->
            when (view) {
                homeButton -> currentLocation = 0
                workButton -> currentLocation = 1
                outsideButton -> currentLocation = 2
                elseButton -> currentLocation = 9
            }
            Timber.i("Current location is: $currentLocation")
            sharedViewModel.locationDialogGetsDismissed()
            dismiss()
        }
        homeButton.setOnClickListener(listener)
        workButton.setOnClickListener(listener)
        outsideButton.setOnClickListener(listener)
        elseButton.setOnClickListener(listener)
    }
}