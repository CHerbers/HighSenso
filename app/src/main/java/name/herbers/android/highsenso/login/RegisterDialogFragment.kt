package name.herbers.android.highsenso.login

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.SharedViewModel
import name.herbers.android.highsenso.databinding.DialogRegisterBinding
import java.text.Collator
import java.util.*

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class RegisterDialogFragment(
    val sharedViewModel: SharedViewModel,
    val loginViewModel: LoginViewModel
) : DialogFragment() {
    private lateinit var binding: DialogRegisterBinding
    private lateinit var editTextList: List<EditText>

    private val invalidInputToast = R.string.login_dialog_invalid_input_toast_message

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
            initCountrySpinner()

            binding.registerDialogPrivacyCheckBox.movementMethod = LinkMovementMethod.getInstance()

            binding.registerDialogCountrySpinner

            val dialog = builder.create()
            dialog
        }
    }

    /**
     * This method creates a list contending every [EditText] of this Dialog.
     *
     * @return a [List] of every [EditText] of this Dialog
     * */
    private fun createEditTextList(): List<EditText> {
        return listOf(
            binding.registerDialogUsernameEditText,
            binding.registerDialogMailEditText,
            binding.registerDialogMailRepeatEditText,
            binding.registerDialogPasswordEditText,
            binding.registerDialogPasswordRepeatEditText
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
            if (noErrorMessageActive()) {
                //TODO Send Registration (Username, Mail, Password, Country)
                // if successful -> navigate
                // else show error
            } else {
                Toast.makeText(context, invalidInputToast, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * This function adds an onClickListener to the LoginButton that calls an associated function
     * on the [SharedViewModel].
     * The Dialog is dismissed after.
     * */
    private fun setLoginButtonListener() {
        binding.registerDialogToLoginButton.setOnClickListener {
            sharedViewModel.handleLoginButtonClick()
            dismiss()
        }
    }

    /**
     * This method checks if there is an error message on any of the [EditText]s in [editTextList].
     *
     * @return true if there is an error message somewhere, false if not
     * */
    private fun noErrorMessageActive(): Boolean {
        var isMessageActive = false
        editTextList.forEach { editText ->
            isMessageActive = isMessageActive || editText.error != ""
        }
        return isMessageActive
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
     * This function initiates the countrySpinner by adding the spinners content Strings and an
     * adapter.
     * */
    private fun initCountrySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            getCountryList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.registerDialogCountrySpinner.adapter = adapter
    }

    /**
     * This function creates a [List] of Strings of all countries available in [Locale].
     * The german speaking countries (Germany, Austria, Switzerland) are put to the start of
     * the List.
     *
     * @return a [List] of country names as Strings
     * */
    private fun getCountryList(): List<String> {
        val countryList: MutableList<String> = mutableListOf()
        val germanSpeakingCountries = listOf(
            "Deutschland",
            "Österreich",
            "Schweiz"
        )
        Locale.getAvailableLocales().forEach { locale ->
            val localeString = locale.getDisplayCountry(Locale.GERMAN)
            if (localeString != "" && !countryList.contains(localeString)) {
                countryList.add(localeString)
            }
        }
        Collections.sort(countryList, Collator.getInstance(Locale.GERMAN))

        for (i in germanSpeakingCountries.indices) {
            val country = germanSpeakingCountries[i]
            countryList.remove(country)
            countryList.add(i, country)
        }
        return countryList
    }

    /**
     * Adds an onChangeListener to a given [EditText].
     * After text is changed an error message is shown if the inserted text does not fit the
     * specific requirements. The error message is created by specific functions in the
     * [LoginViewModel].
     *
     * @param editText is the EditText a Listener is added to
     * */
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