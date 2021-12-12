package name.herbers.android.highsenso.data

/**
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
