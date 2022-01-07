package name.herbers.android.highsenso.questioning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import name.herbers.android.highsenso.SharedViewModel

/**
 * [ViewModelProvider.Factory] for [QuestioningViewModel].
 * Creates a QuestioningViewModel.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
class QuestioningViewModelFactory(
    private val sharedViewModel: SharedViewModel,
    private val startingQuestionPos: Int = 0,
    private val startingQuestionnairePos: Int = 0
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestioningViewModel::class.java)) {
            return QuestioningViewModel(
                sharedViewModel,
                startingQuestionPos,
                startingQuestionnairePos
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}