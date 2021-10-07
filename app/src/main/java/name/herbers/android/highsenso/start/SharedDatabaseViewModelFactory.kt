package name.herbers.android.highsenso.start

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.QuestionDatabaseDao

/**
 * This is the [ViewModelProvider.Factory] for [SharedDatabaseViewModel]
 * @param dataSource the [QuestionDatabaseDao] used for the database in [SharedDatabaseViewModel]
 * @param application the current [Application]
 * */
class SharedDatabaseViewModelFactory(
private val dataSource: QuestionDatabaseDao,
private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedDatabaseViewModel::class.java)) {
            return SharedDatabaseViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}