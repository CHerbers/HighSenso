package name.herbers.android.highsenso.data

/**
 *
 *@project HighSenso
 *@author Herbers
 */
data class Answer(
    val value: String,
    val label: String? = null,
    val collectedAt: Long? = null
)
