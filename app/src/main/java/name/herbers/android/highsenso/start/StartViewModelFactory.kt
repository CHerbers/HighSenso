package name.herbers.android.highsenso.start

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.QuestionDatabaseDao

/**
 * This is the [ViewModelProvider.Factory] for [StartViewModel]
 * @param dataSource the [QuestionDatabaseDao] used for the database in [StartViewModel]
 * @param application the current [Application]
 * */
class StartViewModelFactory(
private val dataSource: QuestionDatabaseDao,
private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartViewModel::class.java)) {
            return StartViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}