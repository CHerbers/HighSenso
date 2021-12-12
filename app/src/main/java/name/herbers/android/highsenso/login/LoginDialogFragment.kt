package name.herbers.android.highsenso.login

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
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
    private lateinit var binding: DialogLoginBinding

    private val badCombinationToast = R.string.login_dialog_bad_combination_toast_message
    private val invalidInputToast = R.string.login_dialog_invalid_input_toast_message

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

            val dialog = builder.create()
//            dialog.setCanceledOnTouchOutside(false)
            dialog
        }
    }

    /**
     *
     * */
    private fun addObservers() {
        sharedViewModel.errorSendingData.observe(this, { s ->
            if (s != "")
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
        })

        sharedViewModel.serverLoginResponse.observe(this, { success ->
            when (success) {
                1 -> dismiss()
                0 -> Toast.makeText(
                    context,
                    badCombinationToast,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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
                sharedViewModel.sendLogin(username, password)
                binding.loginDialogLoginButton.isEnabled = false
                //TODO change color maybe
                //TODO enable button again if login request was denied by server (for whatever reason)
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

}