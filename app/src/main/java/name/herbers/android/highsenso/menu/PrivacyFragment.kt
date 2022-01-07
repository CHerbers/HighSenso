package name.herbers.android.highsenso.menu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.databinding.FragmentPrivacyBinding
import name.herbers.android.highsenso.start.StartFragment
import timber.log.Timber

/**
 * This [Fragment] provides privacy information and privacy setting options for the user.
 * The user can control if sensor data is collected and if this data is sent together with the
 * users result.
 * Privacy settings are stored in [SharedPreferences] keys.
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
        val privacyViewModelFactory = PrivacyViewModelFactory(
            requireActivity().getPreferences(Context.MODE_PRIVATE),
            resources
        )
        viewModel =
            ViewModelProvider(this, privacyViewModelFactory).get(PrivacyViewModel::class.java)
        binding.privacyViewModel = viewModel
        binding.lifecycleOwner = this

        //set title
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.title = resources.getString(R.string.privacy_actionBar_title)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)
        setUpPrivacySwitchCheckedStatus()
        setPrivacySwitchListener()

        Timber.i("PrivacyFragment created!")
        return binding.root
    }

    /**
     * Checks the settings [SwitchCompat] depending of specific key in [SharedPreferences].
     * */
    private fun setUpPrivacySwitchCheckedStatus() {
        binding.privacySettingsSwitch.isChecked = viewModel.getCheckedStatus()
    }

    /**
     * Sets an [View.OnClickListener] to the [SwitchCompat]. If checked or unchecked a specific key in
     * [SharedPreferences] is adjusted.
     * */
    private fun setPrivacySwitchListener() {
        val switch = binding.privacySettingsSwitch
        switch.setOnClickListener {
            viewModel.setPrivacyPreferences(switch.isChecked)
        }
    }

    /**
     * Override fun to define an action when actionBar back button is clicked.
     * If the button is clicked, the app navigated back to the [StartFragment]
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Timber.i("actionBar back button clicked!")
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_privacy_destination_to_start_destination)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}