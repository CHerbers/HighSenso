package name.herbers.android.highsenso.start

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler
import name.herbers.android.highsenso.database.QuestionDatabase

/**
 * This is the [ViewModelProvider.Factory] for [SharedViewModel]
 * @param databaseHandler the [DatabaseHandler] used hold the [QuestionDatabase] instance and
 * handle every action on the database
 * @param application the current [Application]
 * */
class SharedViewModelFactory(
private val databaseHandler: DatabaseHandler,
private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(databaseHandler, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}