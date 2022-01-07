package name.herbers.android.highsenso.data

/**
 * Data class for a logout request.
 * Used to send a logout request to the webserver.
 *
 *@project HighSenso
 *@author Herbers
 */
data class LogoutRequest(
    val token: String
) {
}