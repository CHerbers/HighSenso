package name.herbers.android.highsenso

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import name.herbers.android.highsenso.database.DatabaseHandler
import timber.log.Timber

/**
 * This [AndroidViewModel] belongs to the [MainActivity] and can therefore be accessed by
 * every Fragment of this App under this activity.
 * It is used to provide the [databaseHandler], that is used to handle every
 * database access, to the Fragments and their corresponding ViewModels.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class SharedViewModel(
    val databaseHandler: DatabaseHandler
) : ViewModel() {

    init {
        Timber.i("SharedViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("SharedViewModel destroyed!")
    }
}