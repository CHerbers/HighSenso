package name.herbers.android.highsenso.data

/**
 * Data class for a reset password request.
 * Used to send a reset password request to the webserver.
 *
 *@project HighSenso
 *@author Herbers
 */
data class ResetPasswordRequest(
    val mail: String
)
