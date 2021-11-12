package name.herbers.android.highsenso.start

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.databinding.FragmentStartBinding
import name.herbers.android.highsenso.dialogs.PrivacyDialogFragment
import name.herbers.android.highsenso.dialogs.ResetDialogFragment
import name.herbers.android.highsenso.menu.AboutFragment
import timber.log.Timber

/**The [StartFragment] is the starting [Fragment] of the HighSenso app.
 * This Fragment introduces the user to this App and provides useful information on how to use this
 * App and what this App can and can't do.
 *
 * From this Fragment the user can navigate via the menu to the [AboutFragment] and can start the
 * questioning.
 *
 * Every non-UI task is sourced out to the [StartViewModel].
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class StartFragment : Fragment() {

    private lateinit var startViewModel: StartViewModel
    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i("StartFragment created!")

        //init the DataBinding and ViewModel
        val sharedViewModel: SharedViewModel by activityViewModels()
        val databaseHandler = sharedViewModel.databaseHandler
        val startViewModelFactory = StartViewModelFactory(databaseHandler)
        startViewModel =
            ViewModelProvider(this, startViewModelFactory).get(StartViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)
        binding.startViewModel = startViewModel
        binding.lifecycleOwner = this

        //set title
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.start_actionBar_title)

        //activate menu in this Fragment
        setHasOptionsMenu(true)

        startViewModel.resetDone.observe(viewLifecycleOwner, Observer { resetDone ->
            if (resetDone){
                Toast.makeText(context,
                    R.string.reset_dialog_toast_message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        /* Listener for startButton. Navigation to QuestioningFragment */
        binding.startButton.setOnClickListener { view: View ->
            Timber.i("startButton was clicked!")
            Navigation.findNavController(view)
                .navigate(R.id.action_startFragment_to_questioningFragment)
        }

        //checking if first start of App and calls privacy dialog if so
        privacyCheck()

        // inflate the layout for this Fragment
        return binding.root
    }

    //inflate overflow_menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Timber.i("Overflow menu created!")
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    //handle navigation on item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.i("Menu Item \"${item.title}\" was selected!")
        return when (item.itemId) {
            //reset question ratings
            R.id.reset_rating_destination -> handleResetQuestions()
            //navigate to AboutFragment
            R.id.about_destination -> NavigationUI.onNavDestinationSelected(
                item,
                requireView().findNavController()
            )
            //navigate to PrivacyFragment
            R.id.privacy_destination -> NavigationUI.onNavDestinationSelected(
                item,
                requireView().findNavController()
            )
            //default
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * [ResetDialogFragment] is called to check if the user really wants to reset all ratings.
     * */
    private fun handleResetQuestions(): Boolean {
        ResetDialogFragment(startViewModel).show(childFragmentManager, ResetDialogFragment.TAG)
        return true
    }

    /**
     * This function is checking if its the first start of the App. If so the [PrivacyDialogFragment]
     * is called. This is needed for legal reasons. The user can define the privacy settings.
     * */
    private fun privacyCheck(){
        val preferences = (activity as AppCompatActivity).getPreferences(Context.MODE_PRIVATE)
        val privacyIsFirstCall = preferences.getBoolean(
            getString(R.string.privacy_setting_first_call_key), true
        )
        if (privacyIsFirstCall){
            /* if first call, sets the privacy settings to false per default */
            preferences.edit().putBoolean(
                getString(R.string.privacy_setting_send_general_data_key),
                false
            ).apply()
            Timber.i("General privacy setting set to false!")
            preferences.edit().putBoolean(
                getString(R.string.privacy_setting_send_sensor_data_key),
                false
            ).apply()
            Timber.i("Sensor data privacy setting set to false!")

            /* call privacy dialog */
            PrivacyDialogFragment(preferences).show(childFragmentManager, "PrivacyDialog")
        }
    }
}