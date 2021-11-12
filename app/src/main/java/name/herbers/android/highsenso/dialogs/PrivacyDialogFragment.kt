package name.herbers.android.highsenso.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.DialogPrivacyBinding
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class PrivacyDialogFragment(private val preferences: SharedPreferences) : DialogFragment() {
    private lateinit var binding: DialogPrivacyBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        handlePrivacySettingsFirstCall()
//        setPrivacyPreferences(questioningCheckBox = false, sensorDataCheckBox = false)

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
                    setPrivacyPreferences(
                        binding.privacyQuestioningCheckBox.isChecked,
                        binding.privacySensorCheckBox.isChecked
                    )
                    dialog.dismiss()
                }
                dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            }

            dialog
        }
    }

    /**
     *  This function sets the privacy_settings_first_call_key in the [SharedPreferences] to false.
     *  This is needed because this dialog should only be shown once, on the first start of the
     *  HighSenso App.
     */
    private fun handlePrivacySettingsFirstCall() {
        preferences.edit().putBoolean(
            getString(R.string.privacy_setting_first_call_key),
            false
        ).apply()
        Timber.i("First call set to false!")
    }

    private fun setPrivacyPreferences(questioningCheckBox: Boolean, sensorDataCheckBox: Boolean) {
        preferences.edit().putBoolean(
            getString(R.string.privacy_setting_send_general_data_key),
            questioningCheckBox
        ).apply()
        Timber.i("General privacy setting set to ${questioningCheckBox}!")
        preferences.edit().putBoolean(
            getString(R.string.privacy_setting_send_sensor_data_key),
            sensorDataCheckBox
        ).apply()
        Timber.i("Sensor data privacy setting set to ${sensorDataCheckBox}!")
    }
}