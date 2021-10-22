package name.herbers.android.highsenso.questioning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler

class QuestioningViewModelFactory(
    private val databaseHandler: DatabaseHandler
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestioningViewModel::class.java)) {
            return QuestioningViewModel(databaseHandler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}