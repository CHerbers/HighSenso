package name.herbers.android.highsenso.questioning

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler

/**
 *
 *@project HighSenso
 *@author Herbers
 */
class PersonalQuestioningViewModelFactory(
    private val databaseHandler: DatabaseHandler,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalQuestioningViewModel::class.java)) {
            return PersonalQuestioningViewModel(databaseHandler, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}