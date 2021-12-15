package name.herbers.android.highsenso.login

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.databinding.DialogLoginBinding
import timber.log.Timber

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class LoginDialogFragment(
    val sharedViewModel: SharedViewModel,
    private val loginViewModel: LoginViewModel
) : DialogFragment() {
    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogLoginBinding

    private val badCombinationToast = R.string.login_dialog_bad_combination_toast_message
    private val invalidInputToast = R.string.login_dialog_invalid_input_toast_message

    companion object {
        const val TAG = "LoginDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity.let {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_login, null, false
            )
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)

            builder.setTitle(R.string.login_dialog_title)

            setListeners()
            addObservers()

            dialog = builder.create()
            dialog
        }
    }

    /**
     * Adds two observers to [LiveData] in the [SharedViewModel].
     * The first observer observes if there was an error while sending the login request on app side.
     *
     * The second observer observes the servers response to a sent login request.
     * If login was successful, this [AlertDialog] is dismissed.
     * Else, [handleNegativeLoginResponse] is called to handle with the negative response.
     * */
    private fun addObservers() {
        sharedViewModel.errorSendingData.observe(this, { errorMessage ->
            if (errorMessage != "")
                handleNegativeLoginResponse(errorMessage)
        })

        sharedViewModel.loginResponse.observe(this, { success ->
            when (success) {
                1 -> dismiss()
                0 -> handleNegativeLoginResponse(getString(badCombinationToast))
            }
        })
    }

    /**
     * Calls [elementsAreEnabled] with parameter true to enable all elements.
     * After a [Toast] is shown to tell the user that the username-password-combination was an invalid one.
     * */
    private fun handleNegativeLoginResponse(toastMessage: String) {
        elementsAreEnabled(true)
        Toast.makeText(
            context,
            toastMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * This function sets onClickListeners to the two Buttons of this Dialog as well as on the
     * forgotPasswordTextview.
     * If the TextView is clicked an associated function in the [SharedViewModel] is called.
     * The Dialog is dismissed after.
     *
     * A Click on the LoginButton checks if the login data is valid and calls a method in the
     * [SharedViewModel] to send a login message to the server if so. Otherwise a [Toast] is shown
     * onscreen.
     *
     * A Click on the RegisterButton calls a associated function in the [SharedViewModel] that
     * initiates a change to the [RegisterDialogFragment]. This Dialog is dismissed after.
     * */
    private fun setListeners() {
        /* ClickListener for "forgot password" textView */
        binding.loginDialogForgotPasswordTextView.setOnClickListener {
            Timber.i("forgotPasswordTextView was clicked!")
            sharedViewModel.handleForgotPasswordClick()
            dismiss()
        }

        binding.loginDialogLoginButton.setOnClickListener {
            Timber.i("loginButton was clicked!")
            val username = binding.loginDialogUsernameEditText.text.toString()
            val password = binding.loginDialogPasswordEditText.text.toString()
            if ((loginViewModel.inputUsernameValidation(username) ||
                        loginViewModel.inputMailValidation(username)) &&
                loginViewModel.inputPasswordValidation(password)
            ) {
                sharedViewModel.sendLoginRequest(username, password)
                elementsAreEnabled(false)
            } else {
                Toast.makeText(context, invalidInputToast, Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginDialogRegisterButton.setOnClickListener {
            Timber.i("registerButton was clicked!")
            sharedViewModel.handleRegisterButtonClick()
            dismiss()
        }
    }

    /**
     * This function enables or disables all [Button]s and the [TextView] as well as the possibility
     * to dismiss this [AlertDialog] by touching outside of it.
     * It also changes the color of the login button.
     *
     * @param isEnabled if true, elements are enabled, if false, elements are disabled
     * */
    private fun elementsAreEnabled(isEnabled: Boolean) {
        binding.loginDialogLoginButton.isEnabled = isEnabled
        binding.loginDialogRegisterButton.isEnabled = isEnabled
        binding.loginDialogForgotPasswordTextView.isEnabled = isEnabled
        dialog.setCanceledOnTouchOutside(isEnabled)
        val backgroundColor =
            if (isEnabled) R.color.register_dialog_button_register_color else R.color.register_dialog_disabled_button_color
        binding.loginDialogLoginButton.setBackgroundColor(requireContext().getColor(backgroundColor))
    }

    override fun onDestroy() {
        Timber.i("LoginDialog destroyed!")
        super.onDestroy()
    }

}