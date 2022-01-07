package name.herbers.android.highsenso.data

/**
 * Data class for the client.
 * Used in [AnswerSheet]s.
 *
 *@project HighSenso
 *@author Herbers
 */
data class Client(
    val name: String,
    val device: String,
    val os: String
)
