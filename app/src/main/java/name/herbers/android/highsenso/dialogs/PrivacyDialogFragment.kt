package name.herbers.android.highsenso.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.DialogPrivacyBinding
import name.herbers.android.highsenso.menu.PrivacyFragment
import name.herbers.android.highsenso.menu.PrivacyViewModel
import timber.log.Timber

/**
 * This [DialogFragment] provides the option to define the users privacy settings. Therefore
 * CheckBoxes are shown onscreen that can be checked to allow sending or collecting information.
 * CheckBox changes are executed after the [Dialog.BUTTON_POSITIVE] is clicked.
 * If [Dialog.BUTTON_NEGATIVE] is clicked or this Dialog is closed by clicking somewhere outside this
 * dialog, all privacy settings stay on default, which means no data is sent nor sensor data is
 * collected. This happens regardless of the checked-status of the CheckBoxes.
 *
 * This dialog should only be shown once, this is on the first start of he HighSenso App. Further
 * adjustments on the privacy settings can be done via the [PrivacyFragment].
 *
 *@project HighSenso
 *@author Herbers
 */
class PrivacyDialogFragment(private val viewModel: PrivacyViewModel) : DialogFragment() {

    companion object {
        const val TAG = "PrivacyDialog"
    }

    private lateinit var binding: DialogPrivacyBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Timber.i("$TAG created!")
        viewModel.handlePrivacySettingsFirstCall()

        return activity.let {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_privacy, null, false
            )
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)

            builder.setTitle(R.string.privacy_dialog_title)
            builder.setPositiveButton(R.string.accept_button) { _, _ -> }
            builder.setNegativeButton(R.string.decline_all_button) { _, _ -> }

            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
                    viewModel.setPrivacyPreferences(
                        binding.privacySensorCheckBox.isChecked
                    )
                    dialog.dismiss()
                }
                dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            }
            dialog
        }
    }

    override fun onDestroy() {
        Timber.i("$TAG destroyed!")
        super.onDestroy()
    }
}