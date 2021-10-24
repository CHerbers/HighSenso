package name.herbers.android.highsenso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler

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
private val databaseHandler: DatabaseHandler
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(databaseHandler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}