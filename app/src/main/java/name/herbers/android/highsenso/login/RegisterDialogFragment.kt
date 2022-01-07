package name.herbers.android.highsenso.login

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.connection.ServerCommunicationHandler
import name.herbers.android.highsenso.data.RegistrationRequest
import name.herbers.android.highsenso.data.Settings
import name.herbers.android.highsenso.databinding.DialogRegisterBinding
import name.herbers.android.highsenso.menu.PrivacyFragment
import timber.log.Timber

/**
 * This [DialogFragment] is used to let the user register themselves.
 * Therefore it provides [EditText]s for user input and checks their validity.
 * If the register button is clicked and the input is valid, the [ServerCommunicationHandler] is
 * called to send a register request.
 *
 *@project HighSenso
 *@author Herbers
 */
class RegisterDialogFragment(
    val sharedViewModel: SharedViewModel,
    val loginViewModel: LoginViewModel
) : DialogFragment() {
    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogRegisterBinding
    private lateinit var editTextList: List<EditText?>

    companion object {
        const val TAG = "RegisterDialog"

        const val INVALID_INPUT_TOAST = R.string.login_dialog_invalid_input_toast_message
        const val NAME_ALREADY_USED_TOAST = R.string.register_dialog_name_already_used_toast
        const val EMAIL_ALREADY_USED_TOAST = R.string.register_dialog_email_already_used_toast
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Timber.i("$TAG created!")
        return activity.let {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_register, null, false
            )
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)

            builder.setTitle(R.string.register_dialog_title)
            editTextList = createEditTextList()
            addObservers()
            fillEditTextsFromBackup()
            addAllEditTextListeners()
            setPrivacyTextViewListener()
            setAllButtonListeners()

            binding.registerDialogPrivacyCheckBox.movementMethod = LinkMovementMethod.getInstance()

            binding.registerDialogPrivacyCheckBox.setOnClickListener { }

            dialog = builder.create()
            dialog
        }
    }

    /**
     * Sets a listener to the privacy [TextView].
     * If clicked the given answers get saved, this dialog gets dismissed and the [PrivacyFragment]
     * is shown.
     * */
    private fun setPrivacyTextViewListener() {
        binding.registerDialogPrivacyTextView.setOnClickListener {
            Timber.i("Privacy TextView clicked!")
            backUpEditTexts()
            sharedViewModel.handleRegisterDialogPrivacyClick()
            dismiss()
        }
    }

    /**
     * Adds observers to [SharedViewModel.errorSendingRegisterData] and [SharedViewModel.registerResponse]
     * to perform actions after a register request got answered by the webserver.
     * */
    private fun addObservers() {
        sharedViewModel.errorSendingRegisterData.observe(this, { errorMessage ->
            if (errorMessage != "")
                handleNegativeLoginResponse(errorMessage)
        })
        sharedViewModel.registerResponse.observe(this, { success ->
            when (success) {
                1 -> {
                    dismiss()
                }
                2 -> handleNegativeLoginResponse(getString(NAME_ALREADY_USED_TOAST))
                3 -> handleNegativeLoginResponse(getString(EMAIL_ALREADY_USED_TOAST))
            }
        })
    }

    /**
     * This function is called after the login request was answered negative by the webserver.
     * It enables all element, so the user can edit and click them.
     * Also a [Toast] is shown to tell the user what went wrong.
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
     * This method creates a list contending every [EditText] of this Dialog.
     *
     * @return a [List] of every [EditText] of this Dialog
     * */
    private fun createEditTextList(): List<EditText?> {
        return listOf(
            binding.registerDialogUsernameEditTextLayout.editText,
            binding.registerDialogMailEditTextLayout.editText,
            binding.registerDialogMailRepeatEditTextLayout.editText,
            binding.registerDialogPasswordEditTextLayout.editText,
            binding.registerDialogPasswordRepeatEditTextLayout.editText
        )
    }

    /**
     * Calls functions that set Listeners on all Buttons of this Dialog.
     * */
    private fun setAllButtonListeners() {
        setRegisterButtonListener()
        setLoginButtonListener()
    }

    /**
     * This function initiates the sending of a registration if no error messages are active on the
     * [EditText]s of this Dialog.
     * If there are active error messages, a [Toast] is shown.
     * */
    private fun setRegisterButtonListener() {
        binding.registerDialogRegisterButton.setOnClickListener {
            if (noErrorMessageActive() && binding.registerDialogPrivacyCheckBox.isChecked) {

                sharedViewModel.sendRegistrationRequest(
                    RegistrationRequest(
                        binding.registerDialogUsernameEditTextLayout.editText?.text.toString(),
                        binding.registerDialogMailEditTextLayout.editText?.text.toString(),
                        binding.registerDialogPasswordEditTextLayout.editText?.text.toString(),
                        binding.registerDialogPasswordRepeatEditTextLayout.editText?.text.toString(),
                        Settings("de")
                    )
                )
                elementsAreEnabled(false)
            } else {
                Toast.makeText(context, INVALID_INPUT_TOAST, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * This function enables or disables all [Button]s as well as the possibility
     * to dismiss this [AlertDialog] by touching outside of it.
     * It also changes the color of the login button.
     *
     * @param isEnabled if true, elements are enabled, if false, elements are disabled
     * */
    private fun elementsAreEnabled(isEnabled: Boolean) {
        binding.registerDialogRegisterButton.isEnabled = isEnabled
        binding.registerDialogToLoginButton.isEnabled = isEnabled
        dialog.setCanceledOnTouchOutside(isEnabled)
        val backgroundColor =
            if (isEnabled) R.color.register_dialog_button_register_color else R.color.register_dialog_disabled_button_color
        binding.registerDialogRegisterButton.setBackgroundColor(
            requireContext().getColor(
                backgroundColor
            )
        )
        binding.registerDialogRegisterButton.isEnabled = isEnabled
    }

    /**
     * This function adds an onClickListener to the LoginButton that calls an associated function
     * on the [SharedViewModel].
     * The Dialog is dismissed after.
     * */
    private fun setLoginButtonListener() {
        binding.registerDialogToLoginButton.setOnClickListener {
            sharedViewModel.handleLoginButtonClick()
            backUpEditTexts()
            dismiss()
        }
    }

    /**
     * This method checks if there is an error message on any of the [EditText]s in [editTextList]
     * and if any of the EditTexts is empty.
     *
     * @return true if there is not an error message on any [EditText], nor empty EditTexts, false otherwise
     * */
    private fun noErrorMessageActive(): Boolean {
        editTextList.forEach { editText ->
            if (editText?.error != null || editText?.text.isNullOrBlank()) return false
        }
        Timber.i("No error message active!")
        return true
    }

    /**
     * This function calls another function for every of the five [EditText]s of this
     * Dialog saved in [editTextList] to add a listener on it.
     * */
    private fun addAllEditTextListeners() {
        editTextList.forEach { editText ->
            addEditTextListener(editText)
        }
    }

    /**
     * Adds an onChangeListener to a given [EditText].
     * After text is changed an error message is shown if the inserted text does not fit the
     * specific requirements. The error message is created by specific functions in the
     * [LoginViewModel].
     *
     * @param editText is the EditText a Listener is added to
     * */
    private fun addEditTextListener(editText: EditText?) {
        //EditTexts
        val usernameEditText = binding.registerDialogUsernameEditTextLayout.editText
        val mailEditText = binding.registerDialogMailEditTextLayout.editText
        val mailRepeatEditText = binding.registerDialogMailRepeatEditTextLayout.editText
        val passwordEditText = binding.registerDialogPasswordEditTextLayout.editText
        val passwordRepeatEditText = binding.registerDialogPasswordRepeatEditTextLayout.editText

        editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null) return
                val errorMessage = when (editText) {
                    usernameEditText -> loginViewModel.getUsernameErrorMessage(s.toString())
                    mailEditText -> loginViewModel.getMailErrorMessage(s.toString())
                    mailRepeatEditText -> loginViewModel.getMailRepeatErrorMessage(
                        s.toString(),
                        mailEditText?.text.toString()
                    )
                    passwordEditText -> loginViewModel.getPasswordErrorMessage(s.toString())
                    passwordRepeatEditText -> loginViewModel.getPasswordRepeatErrorMessage(
                        s.toString(),
                        passwordEditText?.text.toString()
                    )
                    else -> ""
                }
                if (errorMessage != "") {
                    editText.error = errorMessage
                } else {
                    editText.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //not needed
            }
        })
    }

    /**
     * This function checks if there are already answers for the given questionnaire.
     * If so the answers are shown in their corresponding [EditText]s.
     * */
    private fun fillEditTextsFromBackup() {
        val map = sharedViewModel.registerDialogBackupMap
        val username = map["username"]
        val email = map["email"]
        val emailRepeat = map["emailRepeat"]
        val password = map["password"]
        val passwordRepeat = map["passwordRepeat"]
        if (username != null)
            binding.registerDialogUsernameEditTextLayout.editText?.setText(username)
        if (email != null)
            binding.registerDialogMailEditTextLayout.editText?.setText(email)
        if (emailRepeat != null)
            binding.registerDialogMailRepeatEditTextLayout.editText?.setText(emailRepeat)
        if (password != null)
            binding.registerDialogPasswordEditTextLayout.editText?.setText(password)
        if (passwordRepeat != null)
            binding.registerDialogPasswordRepeatEditTextLayout.editText?.setText(passwordRepeat)
    }

    /**
     * Given answers in the [EditText]s are saved.
     * */
    private fun backUpEditTexts() {
        sharedViewModel.updateBackupMap(
            binding.registerDialogUsernameEditTextLayout.editText?.text.toString(),
            binding.registerDialogMailEditTextLayout.editText?.text.toString(),
            binding.registerDialogMailRepeatEditTextLayout.editText?.text.toString(),
            binding.registerDialogPasswordEditTextLayout.editText?.text.toString(),
            binding.registerDialogPasswordRepeatEditTextLayout.editText?.text.toString()
        )
    }

    override fun onDestroy() {
        Timber.i("$TAG destroyed!")
        super.onDestroy()
    }
}