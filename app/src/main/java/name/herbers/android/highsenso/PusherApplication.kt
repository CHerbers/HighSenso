package name.herbers.android.highsenso

import android.app.Application
import timber.log.Timber

/**
 * [PusherApplication] is just to plant [Timber] to use it as a *logger* for this application.
 * */
class PusherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}