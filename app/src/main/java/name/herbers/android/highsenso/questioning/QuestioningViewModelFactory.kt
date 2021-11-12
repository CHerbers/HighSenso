package name.herbers.android.highsenso.questioning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler

/**
 * [ViewModelProvider.Factory] for [QuestioningViewModel].
 * Creates a QuestioningViewModel.
 *
 * @param databaseHandler the [DatabaseHandler] that manages all database access
 * @param startingQuestionPos the number of the question that should be shown first
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class QuestioningViewModelFactory(
    private val databaseHandler: DatabaseHandler,
    private val startingQuestionPos: Int = 0
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestioningViewModel::class.java)) {
            return QuestioningViewModel(databaseHandler, startingQuestionPos) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}