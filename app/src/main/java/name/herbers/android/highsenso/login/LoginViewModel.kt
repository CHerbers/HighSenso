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
class LoginViewModel : ViewModel() {

    companion object {
        private const val INVALID_USERNAME_MESSAGE = "Ungültiger Name!"
        private const val INVALID_MAIL_MESSAGE = "Ungültige E-Mail!"
        private const val INVALID_REPEAT_MAIL_MESSAGE = "Nicht-identische E-Mail-Adressen!"
        private const val INVALID_PASSWORD_MESSAGE = "Passwort zu kurz!"
        private const val INVALID_REPEAT_PASSWORD_MESSAGE = "Nicht-identische Passwörter!"
    }

    /**
     * Creates an error message for a given username, depending on its validity.
     *
     * @param name the username the error message is created for
     * @return an error message if invalid, an empty string otherwise
     * */
    fun getUsernameErrorMessage(name: String): String {
        return if (inputUsernameValidation(name)) ""
        else INVALID_USERNAME_MESSAGE
    }

    /**
     * Creates an error message for a given email address, depending on its validity.
     *
     * @param mail the email the error message is created for
     * @return an error message if invalid, an empty string otherwise
     * */
    fun getMailErrorMessage(mail: String): String {
        return if (inputMailValidation(mail)) ""
        else INVALID_MAIL_MESSAGE
    }

    /**
     * Creates an error message for a given email, depending if its identical to the reference email.
     *
     * @param mail the email the error message is created for
     * @param reference the reference email to check for identity
     * @return an error message if not identical, an empty string otherwise
     * */
    fun getMailRepeatErrorMessage(mail: String, reference: String): String {
        return if (mail == reference) ""
        else INVALID_REPEAT_MAIL_MESSAGE
    }

    /**
     * Creates an error message for a given password, depending on its validity.
     *
     * @param password the password the error message is created for
     * @return an error message if invalid, an empty string otherwise
     * */
    fun getPasswordErrorMessage(password: String): String {
        return if (inputPasswordValidation(password)) ""
        else INVALID_PASSWORD_MESSAGE
    }

    /**
     * Creates an error message for a given password, depending if its identical to the reference password.
     *
     * @param password the password the error message is created for
     * @param reference the reference password to check for identity
     * @return an error message if not identical, an empty string otherwise
     * */
    fun getPasswordRepeatErrorMessage(password: String, reference: String): String {
        return if (password == reference) ""
        else INVALID_REPEAT_PASSWORD_MESSAGE
    }

    /**
     * This function checks if the given username is a valid one.
     *
     * @param username the username to check
     * @return true if the username is valid, false otherwise
     * */
    fun inputUsernameValidation(username: String): Boolean {
        //TODO validate name
        return true
    }

    /**
     * This function checks if the given email is a valid one.
     *
     * @param email the email to check
     * @return true if the email is valid, false otherwise
     * */
    fun inputMailValidation(email: String): Boolean {
        if (email.contains('@')) {
            val domain = email.split("@")[1]
            if (domain.contains('.'))
                return domain.split(".")[1].length > 1
        }
        return false
    }

    /**
     * This function checks if the given password is a valid one.
     *
     * @param password the password to check
     * @return true if the password is valid, false otherwise
     * */
    fun inputPasswordValidation(password: String): Boolean {
        //TODO missing validation?
        return password.length > 5
    }
}