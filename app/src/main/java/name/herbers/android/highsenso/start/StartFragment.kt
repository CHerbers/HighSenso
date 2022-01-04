package name.herbers.android.highsenso.start

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.databinding.FragmentStartBinding
import name.herbers.android.highsenso.dialogs.*
import name.herbers.android.highsenso.login.LoginDialogFragment
import name.herbers.android.highsenso.login.LoginViewModel
import name.herbers.android.highsenso.login.RegisterDialogFragment
import name.herbers.android.highsenso.menu.AboutFragment
import name.herbers.android.highsenso.questioning.QuestioningFragment
import timber.log.Timber

/**
 * The [StartFragment] is the starting [Fragment] of the HighSenso app.
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
    private lateinit var preferences: SharedPreferences
    private val loginViewModel = LoginViewModel()
    private var profileMenuItem: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i("StartFragment created!")

        //init DataBinding and ViewModel
        val sharedViewModel: SharedViewModel by activityViewModels()
        val databaseHandler = sharedViewModel.databaseHandler
        val startViewModelFactory = StartViewModelFactory(databaseHandler)
        startViewModel =
            ViewModelProvider(this, startViewModelFactory).get(StartViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)
        binding.startViewModel = startViewModel
        binding.lifecycleOwner = this

        //init SharedPreferences
        preferences = (activity as AppCompatActivity).getPreferences(Context.MODE_PRIVATE)

        //set title
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.title =
                resources.getString(R.string.start_actionBar_title)
            actionBar.setDisplayHomeAsUpEnabled(false)
        }

        //activate menu in this Fragment
        setHasOptionsMenu(true)

        //Observers
        setAllObservers()

        //Listener to navigate to QuestioningFragment
        setAllButtonListeners(sharedViewModel)

        //checking if first start of App and calls privacy dialog if so
        privacyCheck()

        //inflate the layout for this Fragment
        return binding.root
    }

    //inflate overflow_menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Timber.i("Overflow menu created!")
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
        profileMenuItem = menu.findItem(R.id.profile_destination)
    }

    //handle navigation on item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.i("Menu Item \"${item.title}\" was selected!")
        return when (item.itemId) {
            //user profile
            R.id.profile_destination -> NavigationUI.onNavDestinationSelected(
                item,
                requireView().findNavController()
            )
            //reset question ratings
//            R.id.reset_rating_destination -> handleResetQuestions()
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

    private fun setAllButtonListeners(sharedViewModel: SharedViewModel) {
        setStartButtonListener(sharedViewModel)
        setLoginButtonListener(sharedViewModel)
        setRegisterButtonListener(sharedViewModel)
    }

    /**
     * Sets a OnClickListener to the startButton.
     * Navigates to the [QuestioningFragment] to start the questioning.
     * */
    private fun setStartButtonListener(sharedViewModel: SharedViewModel) {
        binding.startButton.setOnClickListener { view: View ->
            Timber.i("startButton was clicked!")
            if (sharedViewModel.isLoggedIn.value == true) {
                if (sharedViewModel.questionnaires.isNullOrEmpty() && false) { //TODO fix
                    /* this is true if no questionnaires could be loaded to this point.
                    * Therefore questioning cannot be started and a message is shown instead */
                    NoQuestionnairesAvailableDialog().show(
                        childFragmentManager,
                        NoQuestionnairesAvailableDialog.TAG
                    )
                } else {
                    if (startViewModel.profileDataAvailable(sharedViewModel.answerSheets)) {
                        LocationDialogFragment(preferences, sharedViewModel).show(
                            childFragmentManager,
                            "LocationDialog"
                        )
                    } else {
                        Navigation.findNavController(view)
                            .navigate(R.id.action_start_destination_to_personalQuestioning_destination)
                    }
                }
            } else {
                showLoginDialog(sharedViewModel)
            }
        }
    }


    /**
     *
     *
     * @param sharedViewModel is the [SharedViewModel] holding function to handle the button click
     * */
    private fun setLoginButtonListener(sharedViewModel: SharedViewModel) {
        binding.startFragmentLoginButton.setOnClickListener {
            Timber.i("loginButton was clicked!")
            if (sharedViewModel.isLoggedIn.value == true) {
                LogoutDialog(sharedViewModel).show(
                    childFragmentManager,
                    LogoutDialog.TAG
                )
            } else {
                sharedViewModel.handleLoginButtonClick()
            }
        }
    }

    /**
     *
     *
     * @param sharedViewModel is the [SharedViewModel] holding function to handle the button click
     * */
    private fun setRegisterButtonListener(sharedViewModel: SharedViewModel) {
        binding.startFragmentRegisterButton.setOnClickListener {
            Timber.i("registerButton was clicked!")
            sharedViewModel.handleRegisterButtonClick()
        }
    }

    /**
     * Calls every fun that sets [Observer]s.
     * */
    private fun setAllObservers() {
        val sharedViewModel: SharedViewModel by activityViewModels()
//        setResetObserver(sharedViewModel)
        setLoginButtonsObserver(sharedViewModel)
        setStartRegisterDialogObserver(sharedViewModel)
        setStartLoginDialogObserver(sharedViewModel)
        setStartResetPasswordDialogObserver(sharedViewModel)
        setStartMailSentDialogObserver(sharedViewModel)
        setLocationDialogObserver(sharedViewModel)
        setStartPrivacyFragmentObserver(sharedViewModel)
    }

    /**
     * Sets an Observer to [StartViewModel.isLoggedIn].
     * If logged in, the registerButton is not shown, the label of the loginButton is set to
     * "logout" and the welcoming text shows the username.
     *
     * If not logged in, the registerButton is shown, the label on the loginButton is "login" and
     * the welcoming text shows a neutral message.
     *
     * @param sharedViewModel is the [SharedViewModel] holding the observed [LiveData]
     * */
    private fun setLoginButtonsObserver(sharedViewModel: SharedViewModel) {
        sharedViewModel.isLoggedIn.observe(viewLifecycleOwner, { isLoggedIn ->
            if (isLoggedIn) {
                binding.startFragmentLoginButton.text =
                    getString(R.string.start_fragment_logout_button)
                binding.startFragmentRegisterButton.visibility = View.INVISIBLE
                binding.startTitleTextView.text =
                    getString(R.string.start_welcome_username_text)
                profileMenuItem?.isVisible =  true
            } else {
                binding.startFragmentLoginButton.text =
                    getString(R.string.start_fragment_login_button)
                binding.startFragmentRegisterButton.visibility = View.VISIBLE
                binding.startTitleTextView.text =
                    getString(R.string.start_welcome_text)
                profileMenuItem?.isVisible =  false
            }
        })
    }

    /**
     * Sets an Observer to [StartViewModel.resetDone]. Shows a [Toast] message onscreen if true.
     * Message tells user that the reset is done.
     * */
    private fun setResetObserver(sharedViewModel: SharedViewModel) {
        sharedViewModel.resetPasswordSent.observe(viewLifecycleOwner, { resetDone ->
            if (resetDone) {
                Toast.makeText(
                    context,
                    R.string.reset_dialog_toast_message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Sets an Observer to [StartViewModel.startResetPasswordDialog]. Starts the [ResetPasswordDialogFragment]
     * if true.
     *
     * @param sharedViewModel is the [SharedViewModel] holding the observed [LiveData]
     * */
    private fun setStartResetPasswordDialogObserver(sharedViewModel: SharedViewModel) {
        sharedViewModel.startResetPasswordDialog.observe(viewLifecycleOwner, { startDialog ->
            if (startDialog) {
                ResetPasswordDialogFragment(sharedViewModel, loginViewModel).show(
                    childFragmentManager,
                    ResetPasswordDialogFragment.TAG
                )
            }
        })
    }

    /**
     * Sets an Observer to [StartViewModel.startRegisterDialog]. Starts the [RegisterDialogFragment]
     * if true.
     *
     * @param sharedViewModel is the [SharedViewModel] holding the observed [LiveData]
     * */
    private fun setStartRegisterDialogObserver(sharedViewModel: SharedViewModel) {
        sharedViewModel.startRegisterDialog.observe(viewLifecycleOwner, { startDialog ->
            if (startDialog) {
                RegisterDialogFragment(sharedViewModel, loginViewModel).show(
                    childFragmentManager,
                    RegisterDialogFragment.TAG
                )
            }
        })
    }

    /**
     * Sets an Observer to [StartViewModel.startLoginDialog]. Starts the [LoginDialogFragment]
     * if true.
     *
     * @param sharedViewModel is the [SharedViewModel] holding the observed [LiveData]
     * */
    private fun setStartLoginDialogObserver(sharedViewModel: SharedViewModel) {
        sharedViewModel.startLoginDialog.observe(viewLifecycleOwner, { startDialog ->
            if (startDialog) {
                showLoginDialog(sharedViewModel)
            }
        })
    }

    private fun showLoginDialog(sharedViewModel: SharedViewModel) {
        LoginDialogFragment(sharedViewModel, loginViewModel).show(
            childFragmentManager,
            LoginDialogFragment.TAG
        )
    }

    private fun setStartMailSentDialogObserver(sharedViewModel: SharedViewModel) {
        sharedViewModel.startSentMailDialog.observe(viewLifecycleOwner, { startDialog ->
            if (startDialog) {
                ConfirmationMailSentDialog().show(
                    childFragmentManager,
                    ConfirmationMailSentDialog.TAG
                )
            }
        })
    }

    /**
     * Sets an Observer to [SharedViewModel.localDialogDismiss]. Navigates to [QuestioningFragment]
     * if true.
     *
     * @param sharedViewModel is the [SharedViewModel] holding the observed [LiveData]
     * */
    private fun setLocationDialogObserver(sharedViewModel: SharedViewModel) {
        sharedViewModel.locationDialogDismiss.observe(viewLifecycleOwner, { dismissed ->
            if (dismissed) {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_startFragment_to_questioningFragment)
            }
        })
    }

    private fun setStartPrivacyFragmentObserver(sharedViewModel: SharedViewModel) {
        sharedViewModel.startPrivacyFragment.observe(viewLifecycleOwner, { toStart ->
            if (toStart) {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_start_destination_to_privacy_destination)
            }
        })
    }

    /**
     * This function is checking if its the first start of the App. If so the [PrivacyDialogFragment]
     * is called. This is needed for legal reasons. The user can define the privacy settings.
     * */
    private fun privacyCheck() {

        val privacyIsFirstCall = preferences.getBoolean(
            getString(R.string.privacy_setting_first_call_key), true
        )
        if (privacyIsFirstCall) {
            /* if first call, sets the privacy settings to false per default */
            preferences.edit().putBoolean(
                getString(R.string.privacy_setting_send_general_data_key),
                false
            ).apply()
            Timber.i("General privacy setting set to false!")
            preferences.edit().putBoolean(
                getString(R.string.privacy_setting_gather_sensor_data_key),
                false
            ).apply()
            Timber.i("Sensor data privacy setting set to false!")

            /* call privacy dialog */
            PrivacyDialogFragment(preferences).show(childFragmentManager, "PrivacyDialog")
        }
    }
}