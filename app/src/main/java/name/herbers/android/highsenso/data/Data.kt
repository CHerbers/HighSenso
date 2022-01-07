package name.herbers.android.highsenso.data

/**
 * Data class for a data object.
 *
 *@project HighSenso
 *@author Herbers
 */
data class Data<T>(
    val type: String,
    val attributes: T
)