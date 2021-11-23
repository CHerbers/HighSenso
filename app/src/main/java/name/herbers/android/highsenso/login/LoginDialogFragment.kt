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

    private val badCombinationToast = "Ungültige Username-Passwort-Kombination!"
    private val invalidInputToast = "Bitte alle Felder korrekt ausfüllen!"

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
            dialog
        }
    }

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
            } else {
                Toast.makeText(context, invalidInputToast, Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginDialogRegisterButton.setOnClickListener {
            Timber.i("registerButton was clicked!")
            sharedViewModel.handleLoginDialogRegisterButtonClick()
            dismiss()
        }


    }

}