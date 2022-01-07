package name.herbers.android.highsenso

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.connection.ServerCommunicationHandler
import name.herbers.android.highsenso.data.AnswerSheet
import name.herbers.android.highsenso.data.Questionnaire
import name.herbers.android.highsenso.database.DatabaseHandler

/**
 * This is the [ViewModelProvider.Factory] for [SharedViewModel].
 * Creates a SharedViewModel.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class SharedViewModelFactory(
    private val databaseHandler: DatabaseHandler,
    private val questionnaires: List<Questionnaire>? = null,
    private val answerSheets: List<AnswerSheet>? = null,
    private val preferences: SharedPreferences,
    private val application: Application,
    private val communicationHandler: ServerCommunicationHandler
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(
                databaseHandler,
                questionnaires,
                answerSheets,
                preferences,
                application,
                communicationHandler
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}