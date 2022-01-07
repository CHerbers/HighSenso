package name.herbers.android.highsenso.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import name.herbers.android.highsenso.R
import name.herbers.android.highsenso.questioning.QuestioningViewModel
import timber.log.Timber

/**
 * This [DialogFragment] is used to ask the user if they are sure they want to end the questioning
 * und navigate to the results.
 *
 *@project HighSenso
 *@author Herbers
 */
class EndQuestioningDialog(val questioningViewModel: QuestioningViewModel) : DialogFragment() {

    companion object {
        const val TAG = "EndQuestioningDialog"
        const val LOGGING_MESSAGE = "EndQuestioningDialog was answered "
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.end_questioning_dialog_title)
            builder.setMessage(R.string.end_questioning_dialog_message)
            builder.setPositiveButton(R.string.positive_button) { _, _ ->
                Timber.i(LOGGING_MESSAGE + "positive!")
                questioningViewModel.navigateToResultFragment()
                dismiss()
            }
            builder.setNegativeButton(R.string.negative_button) { _, _ ->
                Timber.i(LOGGING_MESSAGE + "negative!")
                dismiss()
            }
            builder.create()
        }
    }
}