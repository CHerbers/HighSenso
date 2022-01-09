package name.herbers.android.highsenso.start

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Button
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
import name.herbers.android.highsenso.menu.PrivacyFragment
import name.herbers.android.highsenso.menu.PrivacyViewModel
import name.herbers.android.highsenso.menu.PrivacyViewModelFactory
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i("StartFragment created!")

        /* init DataBinding and ViewModel */
        val sharedViewModel: SharedViewModel by activityViewModels()
        val databaseHandler = sharedViewModel.databaseHandler
        val startViewModelFactory = StartViewModelFactory(databaseHandler)
        startViewModel =
            ViewModelProvider(this, startViewModelFactory).get(StartViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)
        binding.startViewModel = startViewModel
        binding.lifecycleOwner = this

        /* init SharedPreferences */
        preferences = (activity as AppCompatActivity).getPreferences(Context.MODE_PRIVATE)

        /* set title */
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.title =
                resources.getString(R.string.start_actionBar_title)
            actionBar.setDisplayHomeAsUpEnabled(false)
        }

        /* activate menu in this Fragment */
        setHasOptionsMenu(true)

        /* Observers */
        setAllObservers()

        /* Listener to navigate to QuestioningFragment */
        setAllButtonListeners(sharedViewModel)

        /* checking if first start of App and calls privacy dialog if so */
        privacyCheck()

        //inflate the layout for this Fragment
        return binding.root
    }

    /* inflate overflow_menu */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Timber.i("Overflow menu created!")
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    /* handle navigation on item selection */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.i("Menu Item \"${item.title}\" was selected!")
        return when (item.itemId) {
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
     * This function calls every function that sets an [View.OnClickListener] to a [Button].
     *
     * @param sharedViewModel the [SharedViewModel] that provides functionality to the listeners.
     * */
    private fun setAllButtonListeners(sharedViewModel: SharedViewModel) {
        setStartButtonListener(sharedViewModel)
        setLoginButtonListener(sharedViewModel)
        setRegisterButtonListener(sharedViewModel)
    }

    /**
     * Sets a [View.OnClickListener] to the startButton.
     * Navigates to the [QuestioningFragment] to start the questioning if the [Button] was clicked
     * while logged in.
     * Otherwise the [LoginDialogFragment] is shown.
     *
     * @param sharedViewModel the [SharedViewModel] that provides functionality to the listeners.
     * */
    private fun setStartButtonListener(sharedViewModel: SharedViewModel) {
        binding.startButton.setOnClickListener { view: View ->
            Timber.i("startButton was clicked!")
            if (sharedViewModel.isLoggedIn.value == true) {
                if (sharedViewModel.questionnaires.isNullOrEmpty()) {
                    /* this is true if no questionnaires could be loaded to this point.
                    * Therefore questioning cannot be started and a message is shown instead */
                    NoQuestionnairesAvailableDialog().show(
                        childFragmentManager,
                        NoQuestionnairesAvailableDialog.TAG
                    )
                } else {
                    if (startViewModel.baselineAnswerSheetAvailable(sharedViewModel.answerSheets)) {
                        if (sharedViewModel.locationQuestionAvailable()) {
                            LocationDialogFragment(preferences, sharedViewModel).show(
                                childFragmentManager,
                                "LocationDialog"
                            )
                        } else {
                            Navigation.findNavController(view)
                                .navigate(R.id.action_startFragment_to_questioningFragment)
                        }
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
     * Sets a [View.OnClickListener] to the loginButton, that calls
     * [SharedViewModel.handleLoginButtonClick] if the [Button] was clicked while logged in.
     * Shows the [LogoutDialog] otherwise.
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
     * Sets a [View.OnClickListener] to the registerButton, that calls
     * [SharedViewModel.handleRegisterButtonClick] if the [Button] was clicked.
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
     * Calls every function that sets [Observer]s.
     * */
    private fun setAllObservers() {
        val sharedViewModel: SharedViewModel by activityViewModels()
        setLoginButtonsObserver(sharedViewModel)
        setStartRegisterDialogObserver(sharedViewModel)
        setStartLoginDialogObserver(sharedViewModel)
        setStartResetPasswordDialogObserver(sharedViewModel)
        setStartMailSentDialogObserver(sharedViewModel)
        setLocationDialogObserver(sharedViewModel)
        setStartPrivacyFragmentObserver(sharedViewModel)
    }

    /**
     * Sets an Observer to [SharedViewModel.isLoggedIn].
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
            } else {
                binding.startFragmentLoginButton.text =
                    getString(R.string.start_fragment_login_button)
                binding.startFragmentRegisterButton.visibility = View.VISIBLE
                binding.startTitleTextView.text =
                    getString(R.string.start_welcome_text)
            }
        })
    }

    /**
     * Sets an Observer to [SharedViewModel.startResetPasswordDialog]. Starts the [ResetPasswordDialogFragment]
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
     * Sets an Observer to [SharedViewModel.startRegisterDialog]. Shows the [RegisterDialogFragment]
     * if it becomes true.
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
     * Sets an Observer to [SharedViewModel.startLoginDialog]. Shows the [LoginDialogFragment]
     * if it becomes true true.
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

    /**
     * Shows the [LoginDialogFragment].
     * */
    private fun showLoginDialog(sharedViewModel: SharedViewModel) {
        LoginDialogFragment(sharedViewModel, loginViewModel).show(
            childFragmentManager,
            LoginDialogFragment.TAG
        )
    }

    /**
     * Sets an Observer to [SharedViewModel.startSentMailDialog]. Shows the [ConfirmationMailSentDialog]
     * if it becomes true.
     *
     * @param sharedViewModel is the [SharedViewModel] holding the observed [LiveData]
     * */
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
     * Sets an Observer to [SharedViewModel.locationDialogDismiss]. Navigates to [QuestioningFragment]
     * if it becomes true.
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

    /**
     * Sets an Observer to [SharedViewModel.startPrivacyFragment]. Navigates to [PrivacyFragment]
     * if it becomes true.
     *
     * @param sharedViewModel is the [SharedViewModel] holding the observed [LiveData]
     * */
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
            val privacyViewModelFactory = PrivacyViewModelFactory(preferences, resources)
            val privacyViewModel =
                ViewModelProvider(this, privacyViewModelFactory).get(PrivacyViewModel::class.java)
            PrivacyDialogFragment(privacyViewModel).show(
                childFragmentManager,
                PrivacyDialogFragment.TAG
            )
        }
    }
}