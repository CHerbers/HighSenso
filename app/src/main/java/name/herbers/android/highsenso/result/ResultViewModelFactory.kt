package name.herbers.android.highsenso.result

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.SharedViewModel

/**
 * [ViewModelProvider.Factory] for [ResultViewModel].
 * Creates a ResultViewModel.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class ResultViewModelFactory(
    private val sharedViewModel: SharedViewModel,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(sharedViewModel, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}