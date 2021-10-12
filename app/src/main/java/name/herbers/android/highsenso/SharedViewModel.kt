package name.herbers.android.highsenso

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import name.herbers.android.highsenso.database.DatabaseHandler
import timber.log.Timber

/**
 * This [AndroidViewModel] belongs to the [MainActivity] class and can therefore be accessed by
 * every Fragment of this App under this activity.
 * It is used to provide data (especially the [databaseHandler], that is used to handle every
 * database access) to the Fragments and their corresponding ViewModels.
 * */
class SharedViewModel(
    val databaseHandler: DatabaseHandler,
    application: Application
) : AndroidViewModel(application) {

    init {
        Timber.i("SharedViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("SharedViewModel destroyed!")
    }
}