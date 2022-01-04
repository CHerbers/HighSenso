package name.herbers.android.highsenso.questioning

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler

/**
 *[ViewModelProvider.Factory] for [BaselineQuestioningViewModel].
 * Creates a QuestioningViewModel.
 *
 * @param databaseHandler the [DatabaseHandler] that manages all database access
 * @param application the [Application] of the App
 *
 *@project HighSenso
 *@author Herbers
 */
class BaselineQuestioningViewModelFactory(
    private val databaseHandler: DatabaseHandler,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BaselineQuestioningViewModel::class.java)) {
            return BaselineQuestioningViewModel(databaseHandler, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}