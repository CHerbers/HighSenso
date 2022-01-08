package name.herbers.android.highsenso.data

/**
 * Data class for a login request.
 * Used to send a login request to the webserver.
 *
 *@project HighSenso
 *@author Herbers
 */
data class LoginRequest(
    val email: String,
    val password: String
)
