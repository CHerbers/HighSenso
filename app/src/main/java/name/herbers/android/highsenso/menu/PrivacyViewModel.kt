package name.herbers.android.highsenso.menu

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import name.herbers.android.highsenso.R
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class PrivacyViewModel(private val preferences: SharedPreferences, private val res: Resources) :
    ViewModel() {

    init {
        Timber.i("PrivacyViewModel created!")
    }

    /**
     * This function sets the value of the privacy preferences key on the given boolean value.
     *
     * @param sensorDataCheckBox the value privacy_setting_send_sensor_data_key is set to
     * */
    fun setPrivacyPreferences(sensorDataCheckBox: Boolean) {
        preferences.edit().putBoolean(
            res.getString(R.string.privacy_setting_gather_sensor_data_key),
            sensorDataCheckBox
        ).apply()
        Timber.i("Sensor data privacy setting set to ${sensorDataCheckBox}!")
    }

    /**
     *  This function sets the privacy_settings_first_call_key in the [SharedPreferences] to false.
     *  This is needed because this dialog should only be shown once, on the first start of the
     *  HighSenso App.
     */
    fun handlePrivacySettingsFirstCall() {
        preferences.edit().putBoolean(
            res.getString(R.string.privacy_setting_first_call_key),
            false
        ).apply()
        Timber.i("First call set to false!")
    }

    /**
     * Returns if  privacy_setting_gather_sensor_data_key in [SharedPreferences] is true or false.
     *
     * @return the value of the privacy_setting_gather_sensor_data_key in [SharedPreferences]
     * */
    fun getCheckedStatus(): Boolean {
        return preferences.getBoolean(
            res.getString(R.string.privacy_setting_gather_sensor_data_key),
            false
        )
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("PrivacyViewModel destroyed!")
    }
}