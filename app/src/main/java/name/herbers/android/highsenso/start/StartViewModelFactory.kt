package name.herbers.android.highsenso.start

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler

/**
 * [ViewModelProvider.Factory] for [StartViewModel].
 * Creates a StartViewModel.
 * */
class StartViewModelFactory(
    private val databaseHandler: DatabaseHandler,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartViewModel::class.java)) {
            return StartViewModel(databaseHandler, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}