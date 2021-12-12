package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class UpdateProfileRequest(
    val name: String,
    val firstname: String,
    val lastname: String,
    val sex: Int,
    val settings: Settings
) {
}