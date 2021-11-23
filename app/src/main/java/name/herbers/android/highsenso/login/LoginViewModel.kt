package name.herbers.android.highsenso.login

import androidx.lifecycle.ViewModel

/**
 * This is the [ViewModel] for the [LoginDialogFragment] as well as the [RegisterDialogFragment].
 * It holds every non-UI based logic for those Fragments besides functions used for communication
 * with the server. The provided logic especially contains the validation of user inputs.
 *
 *@project HighSenso
 *@author Herbers
 */
class LoginViewModel: ViewModel() {


    fun getUsernameErrorMessage(input: String): String{
        var errorMessage = ""

        return errorMessage
    }

    fun getMailErrorMessage(input: String): String{
        var errorMessage = ""
        return errorMessage
    }

    fun getMailRepeatErrorMessage(input: String, reference: String): String{
        var errorMessage = ""
        return errorMessage
    }

    fun getPasswordErrorMessage(input: String): String{
        var errorMessage = ""
        return errorMessage
    }

    fun getPasswordRepeatErrorMessage(input: String, reference: String): String{
        var errorMessage = ""
        return errorMessage
    }

    fun inputUsernameValidation(username: String): Boolean {
        return true
    }

    fun inputMailValidation(mail: String): Boolean{
        return true
    }

    fun inputPasswordValidation(password: String): Boolean {
        return true
    }

}