package name.herbers.android.highsenso.data

import com.google.gson.Gson

/**
 * Data class for values.
 * Used in [Questionnaire]s.
 * No active use in code, but needed for the [Gson] parsing
 *
 *@project HighSenso
 *@author Herbers
 */
data class Values(
    val min: Int,
    val max: Int,
    val step: Int
)
