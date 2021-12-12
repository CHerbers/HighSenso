package name.herbers.android.highsenso.connection

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Singleton class is needed to not instantiate multiple [RequestQueue]s.
 * This class instantiates one single Queue for this App that is used by for every HTTP-Request.
 *
 * This class is a variant of the example "MySingleton" class from android developers.
 * @see <a href="https://developer.android.com/training/volley/requestqueue">android developers MySingleton</a> *
 *
 *@project HighSenso
 * @since 1.0
 */
class RequestSingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: RequestSingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RequestSingleton(context).also {
                    INSTANCE = it
                }
            }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}