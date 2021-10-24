package name.herbers.android.highsenso.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler

/**
 * [ViewModelProvider.Factory] for [StartViewModel].
 * Creates a StartViewModel.
 *
 * @param databaseHandler the [DatabaseHandler] that manages all database access
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class StartViewModelFactory(
    private val databaseHandler: DatabaseHandler
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartViewModel::class.java)) {
            return StartViewModel(databaseHandler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}