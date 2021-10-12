package name.herbers.android.highsenso.result

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.database.DatabaseHandler

/**
 * [ViewModelProvider.Factory] for [ResultViewModel].
 * Creates a ResultViewModel.
 * */
class ResultViewModelFactory(
    private val databaseHandler: DatabaseHandler,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(databaseHandler, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}