package name.herbers.android.highsenso.data

/**
 * Data class for a registration request.
 * Used to send a registration request to the webserver.
 *
 *@project HighSenso
 *@author Herbers
 */
data class RegistrationRequest(
    val email: String,
    val password: String,
    val password_confirmation: String,
    val username: String,
    val settings: Settings
)
