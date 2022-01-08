package name.herbers.android.highsenso.data

/**
 * Data class for request data objects.
 * Used as the outer object of JSON requests and responds.
 *
 *@project HighSenso
 *@author Herbers
 */
data class RequestData<T>(
    val data: Data<T>
)
