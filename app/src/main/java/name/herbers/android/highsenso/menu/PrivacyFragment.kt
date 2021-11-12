package name.herbers.android.highsenso.menu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.FragmentPrivacyBinding
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class PrivacyFragment : Fragment() {

    private lateinit var binding: FragmentPrivacyBinding
    private lateinit var viewModel: PrivacyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //init the DataBinding and ViewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy, container, false)
        viewModel = ViewModelProvider(this).get(PrivacyViewModel::class.java)
        binding.privacyViewModel = viewModel
        binding.lifecycleOwner = this

        //set title
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.privacy_actionBar_title)

        //init switches (value and Listeners)
        initSwitches()

        Timber.i("AboutFragment created!")
        return binding.root
    }

    /**
     * Initializes the two privacy [Switch]es. Set their starting isChecked value and
     * adds OnCheckedChangeListeners which set the corresponding [SharedPreferences] key
     * to the specific switch value.
     * */
    private fun initSwitches() {
        val generalPrivacyKey = getString(R.string.privacy_setting_send_general_data_key)
        val sensorDataPrivacyKey = getString(R.string.privacy_setting_send_sensor_data_key)
        val preferences = (activity as AppCompatActivity).getPreferences(Context.MODE_PRIVATE)

        binding.privacyGeneralSwitch.isChecked =
            preferences.getBoolean(generalPrivacyKey, false)
        binding.privacyGeneralSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean(generalPrivacyKey, isChecked).apply()
            Timber.i("General privacy setting set to ${isChecked}!")
        }

        binding.privacySensorDataSwitch.isChecked =
            preferences.getBoolean(sensorDataPrivacyKey, false)
        binding.privacySensorDataSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean(sensorDataPrivacyKey, isChecked).apply()
            Timber.i("Sensor data privacy setting set to ${isChecked}!")
        }
    }

}