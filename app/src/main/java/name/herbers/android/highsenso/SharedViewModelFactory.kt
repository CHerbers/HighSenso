package name.herbers.android.highsenso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.PersonalData

/**
 * This is the [ViewModelProvider.Factory] for [SharedViewModel].
 * Creates a SharedViewModel.
 *
 * @param databaseHandler the [DatabaseHandler] that manages all database access
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class SharedViewModelFactory(
    private val databaseHandler: DatabaseHandler,
    private val personalData: PersonalData
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(databaseHandler, personalData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}