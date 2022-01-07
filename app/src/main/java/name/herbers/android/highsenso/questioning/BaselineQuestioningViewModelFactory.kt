package name.herbers.android.highsenso.questioning

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 *[ViewModelProvider.Factory] for [BaselineQuestioningViewModel].
 * Creates a QuestioningViewModel.
 *
 * @param application the [Application] of the App
 *
 *@project HighSenso
 *@author Herbers
 */
class BaselineQuestioningViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BaselineQuestioningViewModel::class.java)) {
            return BaselineQuestioningViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}