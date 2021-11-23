package name.herbers.android.highsenso.login

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.databinding.DialogRegisterBinding

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class RegisterDialogFragment(val sharedViewModel: SharedViewModel, val loginViewModel: LoginViewModel) : DialogFragment() {
    private lateinit var binding: DialogRegisterBinding
    private lateinit var editTextList: List<EditText>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity.let {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.dialog_register, null, false
            )
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)

            builder.setTitle(R.string.register_dialog_title)
            editTextList = createEditTextList()
            addAllEditTextListeners()
            setAllButtonListeners()

            binding.registerDialogPrivacyCheckBox.movementMethod = LinkMovementMethod.getInstance()


            val dialog = builder.create()
            dialog
        }
    }

    private fun createEditTextList(): List<EditText> {
        return listOf(
            binding.registerDialogUsernameEditText,
            binding.registerDialogMailEditText,
            binding.registerDialogMailRepeatEditText,
            binding.registerDialogPasswordEditText,
            binding.registerDialogPasswordRepeatEditText
        )
    }

    private fun setAllButtonListeners(){
        setRegisterButtonListener()
        setLoginButtonListener()
    }

    private fun setRegisterButtonListener() {
        binding.registerDialogRegisterButton.setOnClickListener {
            if (noErrorMessageActive()) {
                //TODO Send Registration
                // if successful -> navigate
                // else show error
            } else {
                //TODO show Toast
            }
        }
    }

    private fun setLoginButtonListener(){
        binding.registerDialogToLoginButton.setOnClickListener {
            sharedViewModel.handleRegisterDialogLoginButtonClick()
            dismiss()
        }
    }

    private fun noErrorMessageActive(): Boolean {
        var isMessageActive = false
        editTextList.forEach { editText ->
            isMessageActive = isMessageActive || editText.error != ""
        }
        return isMessageActive
    }

    private fun addAllEditTextListeners(){
        editTextList.forEach { editText ->
            addEditTextListener(editText)
        }
    }

    private fun addEditTextListener(editText: EditText) {
        //EditTexts
        val usernameEditText = binding.registerDialogUsernameEditText
        val mailEditText = binding.registerDialogMailEditText
        val mailRepeatEditText = binding.registerDialogMailRepeatEditText
        val passwordEditText = binding.registerDialogPasswordEditText
        val passwordRepeatEditText = binding.registerDialogPasswordRepeatEditText

        editText.addTextChangedListener(object : TextWatcher {
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
                        mailEditText.text.toString()
                    )
                    passwordEditText -> loginViewModel.getPasswordErrorMessage(s.toString())
                    passwordRepeatEditText -> loginViewModel.getPasswordRepeatErrorMessage(
                        s.toString(),
                        passwordEditText.text.toString()
                    )
                    else -> ""
                }
                if (errorMessage != "") {
                    editText.error = errorMessage
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //not needed
            }
        })
    }
}